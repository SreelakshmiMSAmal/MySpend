package com.sreelakshmims.myspend

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sreelakshmims.myspend.data.local.MySpendDatabase
import com.sreelakshmims.myspend.di.DatabaseModule
import com.sreelakshmims.myspend.domain.model.NecessityLevel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration3To4Test {

    private lateinit var db: MySpendDatabase

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, MySpendDatabase::class.java)
            .addMigrations(
                MySpendDatabase.MIGRATION_1_2,
                MySpendDatabase.MIGRATION_2_3,
                MySpendDatabase.MIGRATION_3_4
            )
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun databaseCreation_includesBooksCategory() = runBlocking {
        val categoryDao = db.categoryDao
        // Prepopulate initial categories as done in DatabaseModule
        val initialCategories = DatabaseModule.getInitialCategories()
        initialCategories.forEach { categoryDao.insertCategory(it) }

        val categories = categoryDao.getAllCategories().first()
        val booksCategory = categories.find { it.name == "Books" }

        assertNotNull("Books category should exist in DB", booksCategory)
        assertEquals(NecessityLevel.SEMI_NECESSITY, booksCategory?.necessity)
        assertTrue(booksCategory?.isDefault == true)
    }

    @Test
    fun migration3To4_addsBooksCategorySafely() = runBlocking {
        val sqliteDb = db.openHelper.writableDatabase

        // Query categories after migrations have run
        val cursor = sqliteDb.query("SELECT * FROM categories WHERE name = 'Books'")
        assertTrue("Cursor should contain Books category after migration", cursor.moveToFirst())

        val nameIndex = cursor.getColumnIndex("name")
        val necessityIndex = cursor.getColumnIndex("necessity")
        val isDefaultIndex = cursor.getColumnIndex("isDefault")

        assertEquals("Books", cursor.getString(nameIndex))
        assertEquals("SEMI_NECESSITY", cursor.getString(necessityIndex))
        assertEquals(1, cursor.getInt(isDefaultIndex))
        cursor.close()
    }

    @Test
    fun migration3To4_isIdempotent() = runBlocking {
        val sqliteDb = db.openHelper.writableDatabase

        // Execute migration logic again
        MySpendDatabase.MIGRATION_3_4.migrate(sqliteDb)

        val cursor = sqliteDb.query("SELECT COUNT(*) FROM categories WHERE name = 'Books'")
        assertTrue(cursor.moveToFirst())
        val count = cursor.getInt(0)
        cursor.close()

        assertEquals("Books category should not be duplicated", 1, count)
    }
}
