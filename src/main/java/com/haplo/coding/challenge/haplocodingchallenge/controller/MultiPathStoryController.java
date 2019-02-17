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
	private static HashMap<String, HashMap<String, StoryModel>> usersStoriesMap = new HashMap<>();
	
	@GetMapping(path = "/story") 
	public ModelAndView story(@CookieValue(value = "userID", defaultValue = "0000") String userID,
			HttpServletResponse response, @RequestParam(value = "centre", defaultValue = "This is the start of story.") String centre, 
			@RequestParam(value = "pageCode", defaultValue = "00") String pageCode) {
		
		userID = generateUserIdAndSetCookie(userID, response);
		
		if(!usersStoriesMap.get(userID).containsKey("00")) { //base case
			usersStoriesMap.get(userID).put(pageCode, new StoryModel(centre,null,null,null,null));
		}
		
		return generateUpdatedHTML(usersStoriesMap.get(userID),centre, pageCode);	
	}
	
	@PostMapping(path = "/story")
	public ModelAndView onSubmit(@CookieValue(value = "userID", defaultValue = "0000") String userID,
			HttpServletResponse response, @RequestParam(value = "centre") String centre,
			@RequestParam(value = "pageCode", defaultValue = "00") String pageCode,
			@ModelAttribute("storyModel") StoryModel storyModel) {
		
		userID = generateUserIdAndSetCookie(userID, response);
		
		updateStoryMap(storyModel,usersStoriesMap.get(userID),centre, pageCode);
		return generateUpdatedHTML(usersStoriesMap.get(userID),centre,pageCode);
	}
	
	private String generateRandonUserId() {
		return Integer.toString(new Random().nextInt(100000000));
	}
	
	private String generateUpdatedPageCode(String pageCode, String sentenceType) {
		int nextPageNum = Integer.parseInt(pageCode.substring(0,1)) + 1;
		String nextPageNumString = Integer.toString(nextPageNum);
		if(sentenceType.equals("top")) {
			return nextPageNumString + 't';
		}
		if(sentenceType.equals("bottom")) {
			return nextPageNumString + 'b';
		}
		if(sentenceType.equals("left")) {
			return nextPageNumString + 'l';
		}
		if(sentenceType.equals("right")) {
			return nextPageNumString + 'r';
		}
		return pageCode;
	}
	
	private void updateStoryMap(StoryModel storyModel, HashMap<String, StoryModel> storyMap, String centre, String pageCode){
		if(!storyMap.containsKey(pageCode)) {
			storyMap.put(pageCode, new StoryModel());
		}
		storyMap.get(pageCode).setCentre(centre);	
		
		
		if(storyModel.getTop() != null && !storyModel.getTop().isEmpty()) {
			storyMap.get(pageCode).setTop(storyModel.getTop()); //update hashmap for current pagecode
			//update hashmap for updated pagecode
			storyMap.put(generateUpdatedPageCode(pageCode,"top"), new StoryModel(storyModel.getTop(),null,null,null,null)); 
		}
		if(storyModel.getBottom() != null && !storyModel.getBottom().isEmpty()) {
			storyMap.get(pageCode).setBottom(storyModel.getBottom());
			storyMap.put(generateUpdatedPageCode(pageCode,"bottom"), new StoryModel (storyModel.getBottom(),null,null,null,null));
		}
		if(storyModel.getLeft() != null && !storyModel.getLeft().isEmpty()) {
			storyMap.get(pageCode).setLeft(storyModel.getLeft());
			storyMap.put(generateUpdatedPageCode(pageCode,"left"), new StoryModel(storyModel.getLeft(),null,null,null,null));
		}
		if(storyModel.getRight() != null && !storyModel.getRight().isEmpty()) {
			storyMap.get(pageCode).setRight(storyModel.getRight());
			storyMap.put(generateUpdatedPageCode(pageCode,"right"), new StoryModel(storyModel.getRight(),null,null,null,null));
		}	
	}
	
	
	private ModelAndView generateUpdatedHTML(HashMap<String, StoryModel> storyMap, String centre, 
			String pageCode) {
		
		ModelAndView mav = new ModelAndView("story");
		mav.addObject("centre",centre);
		mav.addObject("currentPageCode",pageCode);
		mav.addObject("topUpdatedPageCode",generateUpdatedPageCode(pageCode,"top"));
		mav.addObject("bottomUpdatedPageCode",generateUpdatedPageCode(pageCode,"bottom"));
		mav.addObject("leftUpdatedPageCode",generateUpdatedPageCode(pageCode,"left"));
		mav.addObject("rightUpdatedPageCode",generateUpdatedPageCode(pageCode,"right"));
			
		StoryModel mapValues = storyMap.get(pageCode);
		if(mapValues != null) {
			mav.addObject("top", mapValues.getTop());
			mav.addObject("bottom", mapValues.getBottom());
			mav.addObject("left",mapValues.getLeft());
			mav.addObject("right",mapValues.getRight());
		}
		return mav;	
	}
	
	private String generateUserIdAndSetCookie(String userID, HttpServletResponse response) {
		if(userID.equals("0000")) {
			userID = generateRandonUserId();
			response.addCookie(new Cookie("userID", userID));
		}
		if(!usersStoriesMap.containsKey(userID)) {
			usersStoriesMap.put(userID, new HashMap<String,StoryModel>());
		}
		return userID;
	}

	@Override
	public String getErrorPath() {
		System.out.println(PATH);
		return PATH;
	}

}

