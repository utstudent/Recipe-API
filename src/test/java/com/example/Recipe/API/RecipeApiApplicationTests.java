package com.example.Recipe.API;

import com.example.Recipe.API.models.Ingredient;
import com.example.Recipe.API.models.Recipe;
import com.example.Recipe.API.models.Review;
import com.example.Recipe.API.models.SecurityModels.CustomUserDetails;
import com.example.Recipe.API.models.Step;
import com.example.Recipe.API.repositories.RecipeRepo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = RecipeApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = "test")
class RecipeApiApplicationTests {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	RecipeRepo recipeRepo;

	@Test
	@Order(1)
	public void testGetRecipeByIdSuccessBehavior() throws Exception {
		final long recipeId = 1;

		//set up GET request
		mockMvc.perform(get("/recipes/" + recipeId))

				//print response
				.andDo(print())
				//expect status 200 OK
				.andExpect(status().isOk())
				//expect return Content-Type header as application/json
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				//confirm returned JSON values
				.andExpect(jsonPath("id").value(recipeId))
				.andExpect(jsonPath("minutesToMake").value(2))
				.andExpect(jsonPath("username").value("mark"))
				.andExpect(jsonPath("reviews", hasSize(1)))
				.andExpect(jsonPath("ingredients", hasSize(1)))
				.andExpect(jsonPath("steps", hasSize(2)));
	}

	@Test
	@Order(2)
	public void testGetRecipeByIdFailureBehavior() throws Exception {
		final long recipeId = 5000;

		//set up guaranteed to fail in testing environment request
		mockMvc.perform(get("/recipes/" + recipeId))

				//print response
				.andDo(print())
				//expect status 404 NOT FOUND
				.andExpect(status().isNotFound())
				//confirm that HTTP body contains correct error message
				.andExpect(content().string(containsString("No recipe with ID " + recipeId + " could be found.")));
	}

	@Test
	@Order(3)
	public void testGetAllRecipesSuccessBehavior() throws Exception {
		//set up get request for all recipe endpoint
		this.mockMvc.perform(get("/recipes"))

				//expect status is 200 OK
				.andExpect(status().isOk())

				//expect it will be returned as JSON
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				//expect there are 4 entries
				.andExpect(jsonPath("$", hasSize(4)))

				//expect the first entry to have ID 1
				.andExpect(jsonPath("$[0].id").value(1))

				//expect the first entry to have name test recipe
				.andExpect(jsonPath("$[0].name").value("test recipe"))

				//expect the second entry to have id 2
				.andExpect(jsonPath("$[1].id").value(6))

				//expect the second entry to have a minutesToMake value of 2
				.andExpect(jsonPath("$[1].minutesToMake").value(2))

				//expect the third entry to have id 3
				.andExpect(jsonPath("$[2].id").value(9))

				//expect the third entry to have difficulty rating
				.andExpect(jsonPath("$[2].difficultyRating").value(5));
	}

	@Test
	@Order(4)
	public void testCreateNewRecipeSuccessBehavior() throws Exception {
		Ingredient ingredient = Ingredient.builder().name("brown sugar").state("dry").amount("1 cup").build();
		Step step1 = Step.builder().description("heat pan").stepNumber(1).build();
		Step step2 = Step.builder().description("add sugar").stepNumber(2).build();
		CustomUserDetails user1 = CustomUserDetails.builder().username("billy").password("password").build();

		Review review = Review.builder().description("was just caramel").rating(3).user(user1).build();

		Recipe recipe = Recipe.builder()
				.name("caramel in a pan")
				.difficultyRating(10)
				.minutesToMake(2)
				.ingredients(Set.of(ingredient))
				.steps(Set.of(step1, step2))
				.reviews(Set.of(review))
				.user(user1)
				.build();

		MockHttpServletResponse response =
				this.mockMvc.perform(post("/recipes")
								//set request Content-Type header
								.contentType("application/json")
								//set HTTP body equal to JSON based on recipe object
								.content(TestUtil.convertObjectToJsonBytes(recipe))
						)

						//confirm HTTP response meta
						.andExpect(status().isCreated())
						.andExpect(content().contentType("application/json"))
						//confirm Location header with new location of object matches the correct URL structure
						.andExpect(header().string("Location", containsString("http://localhost/recipes/")))

						//confirm some recipe data
						.andExpect(jsonPath("id").isNotEmpty())
						.andExpect(jsonPath("name").value("caramel in a pan"))

						//confirm ingredient data
						.andExpect(jsonPath("ingredients", hasSize(1)))
						.andExpect(jsonPath("ingredients[0].name").value("brown sugar"))
						.andExpect(jsonPath("ingredients[0].amount").value("1 cup"))

						//confirm step data
						.andExpect(jsonPath("steps", hasSize(2)))
						.andExpect(jsonPath("steps[0]").isNotEmpty())
						.andExpect(jsonPath("steps[1]").isNotEmpty())

						//confirm review data
						.andExpect(jsonPath("reviews", hasSize(1)))
						.andExpect(jsonPath("reviews[0].username").value("idk"))
						.andReturn().getResponse();
	}

	@Test
	@Order(5)
	public void testCreateNewRecipeFailureBehavior() throws Exception {

		Recipe recipe = new Recipe();

		//force failure with empty User object
		this.mockMvc.perform(
						post("/recipes")
								//set body equal to empty recipe object
								.content(TestUtil.convertObjectToJsonBytes(recipe))
								//set Content-Type header
								.contentType("application/json")
				)
				//confirm status code 400 BAD REQUEST
				.andExpect(status().isBadRequest())
				//confirm the body only contains a String
				.andExpect(jsonPath("$").isString());
	}

	@Test
	@Order(6)
	public void testGetRecipesByNameSuccessBehavior() throws Exception {

		//set up get request to search for recipes with names including the word recipe
		MvcResult mvcResult = this.mockMvc.perform(get("/recipes/search/recipe"))
				//expect 200 OK
				.andExpect(status().isOk())
				//expect JSON in return
				.andExpect(content().contentType("application/json"))
				//return the MvcResult
				.andReturn();

		//pull json byte array from the result
		byte[] jsonByteArray = mvcResult.getResponse().getContentAsByteArray();
		//convert the json bytes to an array of Recipe objects
		Recipe[] returnedRecipes = TestUtil.convertJsonBytesToObject(jsonByteArray, Recipe[].class);

		//confirm 3 recipes were returned
		assertThat(returnedRecipes.length, is(3));


		for(Recipe r: returnedRecipes) {
			//confirm none of the recipes are null
			assertThat(r, notNullValue());
			//confirm they all have IDs
			assertThat(r.getId(), notNullValue());
			//confirm they all contain recipe in the name
			assertThat(r.getName(), containsString("recipe"));
		}

		//set up get request to search for recipes with names containing potato
		byte[] jsonBytes = this.mockMvc.perform(get("/recipes/search/potato"))
				//expect 200 OK
				.andExpect(status().isOk())
				//expect json
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				//return response byte array
				.andReturn().getResponse().getContentAsByteArray();

		//get recipes as a java array
		returnedRecipes = TestUtil.convertJsonBytesToObject(jsonBytes, Recipe[].class);

		//confirm only one recipe was returned
		assertThat(returnedRecipes.length, is(1));

		//make sure the recipe isn't null
		assertThat(returnedRecipes[0], notNullValue());

		//expect that the name should contain potato
		assertThat(returnedRecipes[0].getName(), containsString("potato"));
	}

	@Test
	@Order(7)
	public void testGetRecipeByNameFailureBehavior() throws Exception {

		byte[] contentAsByteArray = this.mockMvc.perform(get("/recipes/search/should not exist"))
				//expect 404 NOT FOUND
				.andExpect(status().isNotFound())
				//expect only a String in the body
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
				//retrieve content byte array
				.andReturn().getResponse().getContentAsByteArray();

		//convert JSON to String
		String message = new String(contentAsByteArray);

		//confirm error message is correct
		assertThat(message, is("No recipes could be found with that name."));
	}

	@Test
	@Order(8)
	public void testDeleteRecipeByIdSuccessBehavior() throws Exception {
		final long recipeId = 1;
		//get the recipe with ID 3 for future error message confirmation
		byte[] responseByteArr = this.mockMvc.perform(get("/recipes/" + recipeId))
				.andExpect(status().isOk())
				//confirm correct recipe was returned
				.andExpect(jsonPath("id").value(recipeId))
				.andReturn().getResponse().getContentAsByteArray();

		Recipe recipe3 = TestUtil.convertJsonBytesToObject(responseByteArr, Recipe.class);

		//set up delete request
		byte[] deleteResponseByteArr = this.mockMvc.perform(delete("/recipes/" + recipeId))
				//confirm 200 OK was returned
				.andExpect(status().isOk())
				//confirm a String was returned
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
				.andReturn().getResponse().getContentAsByteArray();

		//pull delete message from byte[]
		String returnedDeleteConfirmationMessage = new String(deleteResponseByteArr);

		//confirm the message is as expected using the previously acquired Recipe object
		assertThat(returnedDeleteConfirmationMessage, is("The recipe with ID "  + recipe3.getId() + " and name " + recipe3.getName() + " was deleted."));
	}

	@Test
	@Order(9)
	public void testDeleteRecipeByIdFailureBehavior() throws Exception {
		//force error with invalid ID
		this.mockMvc.perform(delete("/recipes/-1"))
				//expect 400 BAD REQUEST
				.andExpect(status().isBadRequest())
				//expect plain text aka a String
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
				//confirm correct error message
				.andExpect(content().string(is("No recipe with ID -1 could be found. Could not delete.")));
	}

	@Test
//make sure this test runs last
	@Order(11)
	public void testGetAllRecipesFailureBehavior() throws Exception {

		//delete all entries to force error
		recipeRepo.deleteAll();

		//perform GET all recipes
		this.mockMvc.perform(get("/recipes"))

				.andDo(print())

				//expect 404 NOT FOUND
				.andExpect(status().isNotFound())

				//expect error message defined in RecipeService class
				.andExpect(jsonPath("$").value("There are no recipes yet :( feel free to add one though"));
	}

}
