package com.poscodx.jblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/{id}")
public class BlogController {
	
	@ResponseBody
	@RequestMapping({"","/{categoryNo}","/{categoryNo}/{postNo}"})
	public String index(@PathVariable("id") String blogId
			,@PathVariable("categoryNo") String categoryNo
			,@PathVariable("postNo") String postNo) {//categoryNo, postNo 도 가져와야함.Optional로 하면 Null check가능
		return "BlogController.index()";
	}
	
	@ResponseBody
	@RequestMapping("/admin/basic")
	public String adminBasic(@PathVariable("id") String blogId) {
		return "BlogController.adminBasic()";
	}
}
