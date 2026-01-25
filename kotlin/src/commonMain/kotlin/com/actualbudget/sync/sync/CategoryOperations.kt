package com.actualbudget.sync.sync

import com.actualbudget.sync.db.ActualDatabase

/**
 * Category and category group operations for the Actual Budget database.
 *
 * Handles:
 * - Category CRUD (create, update, delete)
 * - Category group CRUD (create, update, delete)
 * - Reordering categories within and across groups
 * - Reordering category groups
 *
 * All mutation methods use the sync engine to create CRDT changes
 * that propagate to the server.
 *
 * @param engine The change engine for creating sync changes
 * @param database The database for reading category data
 */
class CategoryOperations(
    private val engine: ChangeEngine,
    private val database: ActualDatabase
) {
    // ========== Category CRUD ==========

    /**
     * Create a new category.
     *
     * @param id Category ID
     * @param name Category name
     * @param groupId Parent category group ID
     * @param isIncome Whether this is an income category (default: false)
     * @param sortOrder Optional sort order for display ordering
     * @param hidden Whether the category is hidden (default: false)
     * @return The category ID
     */
    fun createCategory(
        id: String,
        name: String,
        groupId: String,
        isIncome: Boolean = false,
        sortOrder: Double? = null,
        hidden: Boolean = false
    ): String {
        engine.createChange("categories", id, "name", name)
        engine.createChange("categories", id, "cat_group", groupId)
        engine.createChange("categories", id, "is_income", if (isIncome) 1 else 0)
        if (sortOrder != null) {
            engine.createChange("categories", id, "sort_order", sortOrder)
        }
        engine.createChange("categories", id, "hidden", if (hidden) 1 else 0)
        engine.createChange("categories", id, "tombstone", 0)
        return id
    }

    /**
     * Update a category field.
     *
     * @param id The category ID
     * @param field The field name to update
     * @param value The new value
     */
    fun updateCategory(id: String, field: String, value: Any?) {
        engine.createChange("categories", id, field, value)
    }

    /**
     * Delete a category (set tombstone).
     *
     * @param id The category ID
     */
    fun deleteCategory(id: String) {
        engine.createChange("categories", id, "tombstone", 1)
    }

    // ========== Category Group CRUD ==========

    /**
     * Create a new category group.
     *
     * @param id Category group ID
     * @param name Category group name
     * @param isIncome Whether this is an income group (default: false for expense)
     * @param sortOrder Optional sort order for display ordering
     * @param hidden Whether the group is hidden (default: false)
     * @return The category group ID
     */
    fun createCategoryGroup(
        id: String,
        name: String,
        isIncome: Boolean = false,
        sortOrder: Double? = null,
        hidden: Boolean = false
    ): String {
        engine.createChange("category_groups", id, "name", name)
        engine.createChange("category_groups", id, "is_income", if (isIncome) 1 else 0)
        if (sortOrder != null) {
            engine.createChange("category_groups", id, "sort_order", sortOrder)
        }
        engine.createChange("category_groups", id, "hidden", if (hidden) 1 else 0)
        engine.createChange("category_groups", id, "tombstone", 0)
        return id
    }

    /**
     * Update a category group field.
     *
     * @param id The category group ID
     * @param field The field name to update
     * @param value The new value
     */
    fun updateCategoryGroup(id: String, field: String, value: Any?) {
        engine.createChange("category_groups", id, field, value)
    }

    /**
     * Delete a category group (set tombstone).
     *
     * @param id The category group ID
     */
    fun deleteCategoryGroup(id: String) {
        engine.createChange("category_groups", id, "tombstone", 1)
    }

    // ========== Reordering ==========

    /**
     * Move a category to a new position within a group or to a different group.
     *
     * @param categoryId The category to move
     * @param newGroupId The target group (can be same as current)
     * @param targetCategoryId The category to insert before, or null to append at end
     */
    fun moveCategory(categoryId: String, newGroupId: String, targetCategoryId: String?) {
        // Get categories in target group
        val categoriesInGroup = database.actualDatabaseQueries
            .getCategoriesInGroupOrdered(newGroupId)
            .executeAsList()
            .filter { it.id != categoryId } // Exclude the category being moved
            .map { it.id to it.sort_order }

        // Calculate new sort order
        val newSortOrder = BudgetUtils.calculateNewSortOrder(categoriesInGroup, targetCategoryId)

        // Update the category
        engine.createChange("categories", categoryId, "sort_order", newSortOrder)
        engine.createChange("categories", categoryId, "cat_group", newGroupId)
    }

    /**
     * Move a category group to a new position.
     *
     * @param groupId The category group to move
     * @param targetGroupId The group to insert before, or null to append at end
     */
    fun moveCategoryGroup(groupId: String, targetGroupId: String?) {
        // Get all category groups
        val groups = database.actualDatabaseQueries
            .getCategoryGroupsOrdered()
            .executeAsList()
            .filter { it.id != groupId } // Exclude the group being moved
            .map { it.id to it.sort_order }

        // Calculate new sort order
        val newSortOrder = BudgetUtils.calculateNewSortOrder(groups, targetGroupId)

        // Update the group
        engine.createChange("category_groups", groupId, "sort_order", newSortOrder)
    }
}
