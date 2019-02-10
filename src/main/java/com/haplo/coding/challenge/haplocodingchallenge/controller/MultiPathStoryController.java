package com.haplo.coding.challenge.haplocodingchallenge.controller;

import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.haplo.coding.challenge.haplocodingchallenge.model.StoryModel;


@Controller
public class MultiPathStoryController implements ErrorController{
	
	private static final String PATH = "/error";
	private static HashMap<String, HashMap<String, String[]>> usersStoriesMap = new HashMap<>();
	
	@GetMapping(path = "/story") 
	public ModelAndView story(@CookieValue(value = "userID", defaultValue = "0000") String userID,
			HttpServletResponse response, @RequestParam(value = "centre", defaultValue = "This is the start of story.") String centre) {
		
		userID = generateUserIdAndSetCookie(userID, response);
		
		return generateUpdatedHTML(usersStoriesMap.get(userID),centre);	
	}
	
	@PostMapping(path = "/story")
	public ModelAndView onSubmit(@CookieValue(value = "userID", defaultValue = "0000") String userID,
			HttpServletResponse response, @RequestParam(value = "centre", defaultValue = "This is the start of story.") String centre,
	@ModelAttribute("storyModel") StoryModel storyModel) {
		
		userID = generateUserIdAndSetCookie(userID, response);
		
		updateStoryMap(storyModel,usersStoriesMap.get(userID),centre);
		return generateUpdatedHTML(usersStoriesMap.get(userID),centre);
	}
	
	private String generateRandonUserId() {
		return Integer.toString(new Random().nextInt(100000000));
	}
	
	private void updateStoryMap(StoryModel storyModel, HashMap<String, String[]> storyMap, String centre){
		if(!storyMap.containsKey(centre)) {
			storyMap.put(centre, new String[4]);
		}
		if(storyModel.getTop() != null && !storyModel.getTop().isEmpty()) {
			storyMap.get(centre)[0] = storyModel.getTop();
		}
		if(storyModel.getBottom() != null && !storyModel.getBottom().isEmpty()) {
			storyMap.get(centre)[1] = storyModel.getBottom();
		}
		if(storyModel.getLeft() != null && !storyModel.getLeft().isEmpty()) {
			storyMap.get(centre)[2] = storyModel.getLeft();
		}
		if(storyModel.getRight() != null && !storyModel.getRight().isEmpty()) {
			storyMap.get(centre)[3] = storyModel.getRight();
		}	
	}
	
	private ModelAndView generateUpdatedHTML(HashMap<String, String[]> storyMap, String centre) {
		ModelAndView mav = new ModelAndView("story");
		mav.addObject("centre",centre);
		String[] mapValuesArray = storyMap.get(centre);
		if(mapValuesArray != null) {
			mav.addObject("top", mapValuesArray[0]);
			mav.addObject("bottom", mapValuesArray[1]);
			mav.addObject("left",mapValuesArray[2]);
			mav.addObject("right",mapValuesArray[3]);
		}
		return mav;	
	}
	
	private String generateUserIdAndSetCookie(String userID, HttpServletResponse response) {
		if(userID.equals("0000")) {
			userID = generateRandonUserId();
			response.addCookie(new Cookie("userID", userID));
		}
		if(!usersStoriesMap.containsKey(userID)) {
			usersStoriesMap.put(userID, new HashMap<String,String[]>());
		}
		return userID;
	}

	@Override
	public String getErrorPath() {
		System.out.println(PATH);
		return PATH;
	}

}
