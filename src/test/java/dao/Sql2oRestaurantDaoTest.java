package dao;

import models.Foodtype;
import org.junit.*;

import models.Restaurant;
import dao.Sql2oFoodtypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;

public class Sql2oRestaurantDaoTest {

    private static Connection conn;
    private static Sql2oRestaurantDao restaurantDao;
    private static Sql2oFoodtypeDao foodtypeDao;
    private static Sql2oReviewDao reviewDao;

    @BeforeClass
    public static void setUp() throws Exception {
        String connectionString = "jdbc:postgresql://localhost:5432/jadle_test";
        Sql2o sql2o = new Sql2o(connectionString, "User", "7181");
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("clearing database");
        restaurantDao.clearAll(); //clear all restaurants after every test
        foodtypeDao.clearAll(); //clear all restaurants after every test
        reviewDao.clearAll(); //clear all restaurants after every test
    }

    @AfterClass //changed to @AfterClass (run once after all tests in this file completed)
    public static void shutDown() throws Exception{ //changed to static
        conn.close(); // close connection once after this entire test file is finished
        System.out.println("connection closed");
    }

    @Test
    public void addingFoodSetsId() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertNotEquals(0, testRestaurant.getId());
    }

    @Test
    public void addedRestaurantsAreReturnedFromGetAll() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertEquals(1, restaurantDao.getAll().size());
    }

    @Test
    public void RestaurantReturnsFoodtypesCorrectly() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Foodtype otherFoodtype  = new Foodtype("Bar Food");
        foodtypeDao.add(otherFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.addRestaurantToFoodtype(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodtype(testRestaurant,otherFoodtype);

        Foodtype[] foodtypes = {testFoodtype, otherFoodtype}; //oh hi what is this? Observe how we use its assertion below.

        assertEquals(Arrays.asList(foodtypes), restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()));
    }

    @Test
    public void noRestaurantsReturnsEmptyList() throws Exception {
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void findByIdReturnsCorrectRestaurant() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        assertEquals(testRestaurant, restaurantDao.findById(testRestaurant.getId()));
    }

    @Test
    public void updateCorrectlyUpdatesAllFields() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.update(testRestaurant.getId(), "a", "b", "c", "d", "e", "f");
        Restaurant foundRestaurant = restaurantDao.findById(testRestaurant.getId());
        assertEquals("a", foundRestaurant.getName());
        assertEquals("b", foundRestaurant.getAddress());
        assertEquals("c", foundRestaurant.getZipcode());
        assertEquals("d", foundRestaurant.getPhone());
        assertEquals("e", foundRestaurant.getWebsite());
        assertEquals("f", foundRestaurant.getEmail());
    }

    @Test
    public void deleteByIdDeletesCorrectRestaurant() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(1, restaurantDao.getAll().size());
    }

    @Test
    public void deletingFoodtypeAlsoUpdatesJoinTable() throws Exception {
        Foodtype testFoodtypeOne  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtypeOne);

        Foodtype testFoodtypeTwo  = new Foodtype("Sushi");
        foodtypeDao.add(testFoodtypeTwo);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);

        restaurantDao.addRestaurantToFoodtype(testRestaurant,testFoodtypeOne);
        restaurantDao.addRestaurantToFoodtype(testRestaurant, testFoodtypeTwo);

        foodtypeDao.deleteById(testFoodtypeTwo.getId());
        assertEquals(0, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtypeTwo.getId()).size());
    }

    @Test
    public void clearAll() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        restaurantDao.clearAll();
        assertEquals(0, restaurantDao.getAll().size());
    }

    //helpers

    public Restaurant setupRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }

    public Restaurant setupAltRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874");
        restaurantDao.add(restaurant);
        return restaurant;
    }

    public Foodtype setupNewFoodtype(){
        return new Foodtype("Sushi");
    }
}