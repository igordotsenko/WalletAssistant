package com.kindhomeless.wa.walletassistant.repo.storage;

import com.kindhomeless.wa.walletassistant.model.Category;
import com.kindhomeless.wa.walletassistant.model.PersistableModel;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CategoryRepoImplTest extends BasicRepoImplTest {
    private static final String TEST_CATEGORY_NAME = "test_name";
    private static final String TEST_CATEGORY_WALLET_ID = "1";
    private CategoryRepo categoryRepo;

    @Before
    public void setUp() {
        super.setUp();
        categoryRepo = RepositoryManager.getInstance().getRepository(CategoryRepo.class);
        assertTrue(categoryRepo.findAll().isEmpty());
    }

    @Test
    public void categorySaveTest() {
        Category categoryToPersist = buildTestCategory();
        long id = categoryRepo.save(categoryToPersist);

        assertEquals(1, id);
        PersistableModel retrievedCategory = categoryRepo.findById(id);
        assertEquals(categoryToPersist, retrievedCategory);
    }

    @Test
    public void categoryNameUniqueConstraintTest() {
        Category initialCategory = buildTestCategory();
        long initialId = categoryRepo.save(initialCategory);
        assertEquals(1, initialId);
        assertEquals(initialCategory, categoryRepo.findById(initialId));

        // Has same name and id - should be updated
        Category sameNameAndIdCategory = new Category(
                initialCategory.getWalletId(), initialCategory.getName(),
                "test_color_2", 43, "test_type_2", 222);
        checkSingleCategoryUpdated(sameNameAndIdCategory);

        // Has same name - should be updated
        String updatedWalletId = "2";
        Category sameNameCategory = new Category(updatedWalletId, initialCategory.getName()
                , "test_color_2", 43, "test_type_2", 222);
        checkSingleCategoryUpdated(sameNameCategory);

        // Has same id - should be updated
        Category sameIdCategory = new Category(updatedWalletId, "another_name", "test_color_3", 44, "test_type_3", 333);
        checkSingleCategoryUpdated(sameIdCategory);
    }

    @Test
    public void findAllTest() {
        List<Category> categoriesToPersist = buildCategoriesList();
        categoryRepo.saveAll(categoriesToPersist);

        List<Category> retrievedCategories = categoryRepo.findAll();
        assertEquals(categoriesToPersist.size(), retrievedCategories.size());
        assertEquals(categoriesToPersist, retrievedCategories);
    }

    @Test
    public void nullColumnsTest() {
        // Test case 1: id is updated from null to some concrete id - record is updated
        Category nullIdCategory = new Category(null, TEST_CATEGORY_NAME, "test_color_1", 42, "test_type_1", 111);
        checkSingleCategoryUpdated(nullIdCategory);
        Category nonNullIdCategory = new Category(TEST_CATEGORY_WALLET_ID, TEST_CATEGORY_NAME, "test_color_2", 43, "test_type_2", 222);
        checkSingleCategoryUpdated(nonNullIdCategory);

        // Test case 2: cannot update back to null id
        categoryRepo.save(nullIdCategory);
        List<Category> allCategories = categoryRepo.findAll();
        assertEquals(1, allCategories.size());
        assertEquals(nonNullIdCategory, allCategories.get(0));

        // Test case 3: cannot insert category with null name
        categoryRepo.deleteAll();
        Category nullNameCategory = new Category("1", null, "test_color_2", 43, "test_type_2", 222);
        categoryRepo.save(nullNameCategory);
        assertTrue(categoryRepo.findAll().isEmpty());
    }

    @Test
    public void deleteAllTest() {
        categoryRepo.saveAll(buildCategoriesList());
        assertFalse(categoryRepo.findAll().isEmpty());
        categoryRepo.deleteAll();
        assertTrue(categoryRepo.findAll().isEmpty());
    }

    @Test
    public void findCategoryByNameTest() {
        Category category = buildTestCategory();
        categoryRepo.save(category);
        assertEquals(category, categoryRepo.findCategoryByName(category.getName()));
    }

    @Test
    public void findCategoryByColumnIdTest() {
        Category category = buildTestCategory();
        categoryRepo.save(category);
        assertEquals(category, categoryRepo.finaByWalletId(category.getWalletId()));
    }

    private List<Category> buildCategoriesList() {
        Category category1 = new Category("1", "1", "1", 1, "1", 1);
        Category category2 = new Category("2", "2", "2", 2, "2", 2);
        Category category3 = new Category("3", "3", "3", 3, "3", 3);
        return asList(category1, category2, category3);
    }

    private void checkSingleCategoryUpdated(Category updatedCategory) {
        long updatedId = categoryRepo.save(updatedCategory);
        assertEquals(updatedCategory, categoryRepo.findById(updatedId));
        assertEquals(1, categoryRepo.findAll().size());
    }

    private Category buildTestCategory() {
        return new Category(TEST_CATEGORY_WALLET_ID, TEST_CATEGORY_NAME, "test_color", 42, "test_type", 22);
    }
}