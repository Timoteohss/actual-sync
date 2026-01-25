package com.actualbudget.sync.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for CategoryOperations.
 *
 * Tests category and category group CRUD and reordering.
 * Uses an in-memory SQLite database for fast, isolated tests.
 */
class CategoryOperationsTest {

    // ========== Category CRUD Tests ==========

    @Test
    fun testCreateCategory_basic() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        val catId = ops.createCategory(
            id = "new-cat-1",
            name = "Entertainment",
            groupId = "group-2"
        )

        assertEquals("new-cat-1", catId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-1" && it.column == "name" && it.value == "Entertainment" })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-1" && it.column == "cat_group" && it.value == "group-2" })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-1" && it.column == "is_income" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-1" && it.column == "hidden" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-1" && it.column == "tombstone" && it.value == 0 })
    }

    @Test
    fun testCreateCategory_withAllOptions() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        val catId = ops.createCategory(
            id = "new-cat-2",
            name = "Bonus",
            groupId = "group-income",
            isIncome = true,
            sortOrder = 5000.0,
            hidden = true
        )

        assertEquals("new-cat-2", catId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-2" && it.column == "name" && it.value == "Bonus" })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-2" && it.column == "cat_group" && it.value == "group-income" })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-2" && it.column == "is_income" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-2" && it.column == "sort_order" && it.value == 5000.0 })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "new-cat-2" && it.column == "hidden" && it.value == 1 })
    }

    @Test
    fun testUpdateCategory_name() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        ops.updateCategory("cat-rent", "name", "Rent & Mortgage")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-rent" && it.column == "name" && it.value == "Rent & Mortgage" })
    }

    @Test
    fun testUpdateCategory_hidden() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        ops.updateCategory("cat-rent", "hidden", 1)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-rent" && it.column == "hidden" && it.value == 1 })
    }

    @Test
    fun testDeleteCategory() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        ops.deleteCategory("cat-rent")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-rent" && it.column == "tombstone" && it.value == 1 })
    }

    // ========== Category Group CRUD Tests ==========

    @Test
    fun testCreateCategoryGroup_basic() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        val groupId = ops.createCategoryGroup(
            id = "new-group-1",
            name = "Entertainment"
        )

        assertEquals("new-group-1", groupId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "new-group-1" && it.column == "name" && it.value == "Entertainment" })
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "new-group-1" && it.column == "is_income" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "new-group-1" && it.column == "hidden" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "new-group-1" && it.column == "tombstone" && it.value == 0 })
    }

    @Test
    fun testCreateCategoryGroup_withAllOptions() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        val groupId = ops.createCategoryGroup(
            id = "new-group-2",
            name = "Other Income",
            isIncome = true,
            sortOrder = 10000.0,
            hidden = true
        )

        assertEquals("new-group-2", groupId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "new-group-2" && it.column == "name" && it.value == "Other Income" })
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "new-group-2" && it.column == "is_income" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "new-group-2" && it.column == "sort_order" && it.value == 10000.0 })
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "new-group-2" && it.column == "hidden" && it.value == 1 })
    }

    @Test
    fun testUpdateCategoryGroup_name() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        ops.updateCategoryGroup("group-1", "name", "Monthly Bills")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "group-1" && it.column == "name" && it.value == "Monthly Bills" })
    }

    @Test
    fun testDeleteCategoryGroup() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        ops.deleteCategoryGroup("group-1")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "group-1" && it.column == "tombstone" && it.value == 1 })
    }

    // ========== Move Category Tests ==========

    @Test
    fun testMoveCategory_withinSameGroup() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move Utilities (sort_order 2000) before Rent (sort_order 1000) within group-1
        ops.moveCategory("cat-utilities", "group-1", "cat-rent")

        val changes = engine.getChanges()
        // Should have new sort_order less than 1000 (half of 1000 = 500)
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-utilities" && it.column == "sort_order" })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-utilities" && it.column == "cat_group" && it.value == "group-1" })

        // Verify sort_order is less than rent's 1000
        val sortOrderChange = changes.find { it.row == "cat-utilities" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        assertTrue(newSortOrder < 1000.0, "Expected sort_order < 1000, got $newSortOrder")
    }

    @Test
    fun testMoveCategory_toDifferentGroup() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move Rent from group-1 (Bills) to group-2 (Food)
        ops.moveCategory("cat-rent", "group-2", null)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-rent" && it.column == "cat_group" && it.value == "group-2" })
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-rent" && it.column == "sort_order" })
    }

    @Test
    fun testMoveCategory_appendAtEnd() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move Rent to end of group-1 (after Utilities which has sort_order 2000)
        ops.moveCategory("cat-rent", "group-1", null)

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "cat-rent" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Should be greater than Utilities' 2000 (2000 + 16384 = 18384)
        assertTrue(newSortOrder > 2000.0, "Expected sort_order > 2000, got $newSortOrder")
    }

    @Test
    fun testMoveCategory_betweenCategories() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedCategories(db)

        // Add a third category to group-1 with sort_order 3000
        db.actualDatabaseQueries.insertCategory(
            id = "cat-insurance",
            name = "Insurance",
            cat_group = "group-1",
            is_income = 0,
            sort_order = 3000.0,
            hidden = 0,
            tombstone = 0
        )

        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move Insurance between Rent (1000) and Utilities (2000)
        ops.moveCategory("cat-insurance", "group-1", "cat-utilities")

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "cat-insurance" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Should be between 1000 and 2000 (midpoint = 1500)
        assertTrue(newSortOrder > 1000.0 && newSortOrder < 2000.0, "Expected 1000 < sort_order < 2000, got $newSortOrder")
    }

    @Test
    fun testMoveCategory_toEmptyGroup() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedCategories(db)

        // Create an empty group
        db.actualDatabaseQueries.insertCategoryGroup(
            id = "group-empty",
            name = "Empty Group",
            is_income = 0,
            sort_order = 5000.0,
            hidden = 0,
            tombstone = 0
        )

        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move Rent to empty group
        ops.moveCategory("cat-rent", "group-empty", null)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-rent" && it.column == "cat_group" && it.value == "group-empty" })
        // Sort order should be SORT_INCREMENT (16384)
        val sortOrderChange = changes.find { it.row == "cat-rent" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        assertEquals(16384.0, newSortOrder)
    }

    // ========== Move Category Group Tests ==========

    @Test
    fun testMoveCategoryGroup_toBeginning() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move Food (group-2, sort_order 2000) before Bills (group-1, sort_order 1000)
        ops.moveCategoryGroup("group-2", "group-1")

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "group-2" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Should be less than 1000 (half of 1000 = 500)
        assertTrue(newSortOrder < 1000.0, "Expected sort_order < 1000, got $newSortOrder")
    }

    @Test
    fun testMoveCategoryGroup_toEnd() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move Bills (group-1) to end
        ops.moveCategoryGroup("group-1", null)

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "group-1" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Should be greater than Income's 3000 (3000 + 16384 = 19384)
        assertTrue(newSortOrder > 3000.0, "Expected sort_order > 3000, got $newSortOrder")
    }

    @Test
    fun testMoveCategoryGroup_between() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move Income (group-income, sort_order 3000) between Bills (1000) and Food (2000)
        ops.moveCategoryGroup("group-income", "group-2")

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "group-income" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Should be between 1000 and 2000 (midpoint = 1500)
        assertTrue(newSortOrder > 1000.0 && newSortOrder < 2000.0, "Expected 1000 < sort_order < 2000, got $newSortOrder")
    }

    // ========== Edge Cases ==========

    @Test
    fun testCreateCategory_noSortOrder() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        ops.createCategory(
            id = "cat-no-sort",
            name = "No Sort",
            groupId = "group-1"
        )

        val changes = engine.getChanges()
        // Should NOT have a sort_order change (it's null/omitted)
        val sortOrderChange = changes.find { it.row == "cat-no-sort" && it.column == "sort_order" }
        assertEquals(null, sortOrderChange)
    }

    @Test
    fun testCreateCategoryGroup_noSortOrder() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        ops.createCategoryGroup(
            id = "group-no-sort",
            name = "No Sort Group"
        )

        val changes = engine.getChanges()
        // Should NOT have a sort_order change
        val sortOrderChange = changes.find { it.row == "group-no-sort" && it.column == "sort_order" }
        assertEquals(null, sortOrderChange)
    }

    @Test
    fun testMoveCategory_targetNotFound() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move with non-existent target - should append at end
        ops.moveCategory("cat-rent", "group-1", "non-existent-category")

        val changes = engine.getChanges()
        // Should still create changes (appends at end)
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-rent" && it.column == "sort_order" })
    }

    @Test
    fun testMoveCategoryGroup_targetNotFound() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        // Move with non-existent target - should append at end
        ops.moveCategoryGroup("group-1", "non-existent-group")

        val changes = engine.getChanges()
        // Should still create changes (appends at end)
        assertTrue(changes.any { it.dataset == "category_groups" && it.row == "group-1" && it.column == "sort_order" })
    }

    @Test
    fun testUpdateCategory_nullValue() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = CategoryOperations(engine, db)

        ops.updateCategory("cat-rent", "goal", null)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "categories" && it.row == "cat-rent" && it.column == "goal" && it.value == null })
    }
}
