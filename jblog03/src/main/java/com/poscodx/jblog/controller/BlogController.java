package com.poscodx.jblog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.poscodx.jblog.security.Auth;
import com.poscodx.jblog.security.AuthUser;
import com.poscodx.jblog.service.BlogService;
import com.poscodx.jblog.vo.BlogVo;
import com.poscodx.jblog.vo.CategoryVo;
import com.poscodx.jblog.vo.PostVo;
import com.poscodx.jblog.vo.UserVo;
import com.poscodx.jblog.service.FileUploadService;
import com.poscodx.jblog.service.UserService;

@Controller
@RequestMapping("/{id:^(?!assets|admin).*$}")
public class BlogController {
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private BlogService blogService;
	@Autowired
	private UserService userService;	
	@Autowired
	private FileUploadService fileuploadService;
	
	@RequestMapping({"","/{categoryNo}","/{categoryNo}/{postNo}"})
	public String index(@PathVariable("id") String blogId
			,@PathVariable("categoryNo") Optional<String> categoryNo
			,@PathVariable("postNo") Optional<String> postNo,
			Model model) {//categoryNo, postNo 도 가져와야함.Optional로 하면 Null check가능
		System.out.println("*********************");
		System.out.println("categoryNo = "+categoryNo+", postNo = "+postNo);
		System.out.println("*********************");
		
		boolean chkId=userService.findById(blogId);
		if(!chkId) {
			model.addAttribute("message","존재하지 않는 블로그 주소입니다.");
			return "error/404";
		}
		
		BlogVo blogVo = blogService.getBlogById(blogId);
		model.addAttribute("blogVo",blogVo);
		List<Map<String, Object>> cVo = blogService.getCategoryById(blogId);
		model.addAttribute("cList",cVo);
		Map<String,Object> map = new HashMap();
		map.put("blogId", blogId);
		if(categoryNo.isEmpty()&&postNo.isEmpty()){//카테고리,포스트번호 지정 X
			List<PostVo> pList=blogService.getPost(map);
			System.out.println(">> pList = "+pList);
			model.addAttribute("pList", pList);
		}else  if(categoryNo.isPresent()&&postNo.isEmpty()) {//카테코리만 지정
			map.put("categoryNo", categoryNo.get());
			List<PostVo> pList=blogService.getPost(map);
			System.out.println(">> pList = "+pList);
			model.addAttribute("pList", pList);
		}else {//카테고리,포스트 지정O
			map.put("categoryNo", categoryNo.get());
			map.put("postNo", postNo.get());
			List<PostVo> pList=blogService.getPost(map);
			System.out.println(">> pList = "+pList);
			model.addAttribute("pList", pList);
		}
		return "blog/main";

	}
	
	@Auth
	@RequestMapping("/admin/basic")
	public String adminBasic(@AuthUser UserVo authUser,	
			@PathVariable("id") String blogId, Model model) {
		BlogVo blogVo = blogService.getBlogById(blogId);
		model.addAttribute("blogVo",blogVo);
		return "blog/admin-basic";
	}
	
	@Auth
	@RequestMapping(value="/admin/basic",method=RequestMethod.POST)
	public String adminBasic(@AuthUser UserVo authUser,	
			@PathVariable("id") String blogId, 
			BlogVo blogVo
			,MultipartFile file) {
		
		String profile = fileuploadService.restore(file);
		if(profile != null) {
			blogVo.setImage(profile);
		}
		
		BlogVo blog = applicationContext.getBean(BlogVo.class);

		System.out.println(">>admin페이지 blog내용 업데이트 "+blogVo);
		blogService.update(blogVo);
		servletContext.setAttribute("blogVo", blogVo);
		BeanUtils.copyProperties(blogVo, blog);

		return "blog/admin-basic";
	}
	
	@Auth
	@RequestMapping("/admin/category")
	public String adminCategory(@AuthUser UserVo authUser,	
			@PathVariable("id") String blogId, Model model) {
		BlogVo blogVo = blogService.getBlogById(blogId);
		model.addAttribute("blogVo",blogVo);
		System.out.println(">> blogVo = "+blogVo);
		System.out.println(">> blogUser = "+authUser.getId());
		List<Map<String, Object>> cVo = blogService.getCategoryById(authUser.getId());
		model.addAttribute("list",cVo);
		System.out.println(">> cVo = "+cVo);
		
		return "blog/admin-category";
	}
	
	@Auth
	@RequestMapping("/admin/category_delete/{no}")
	public String deleteCategory(@PathVariable("no") Long no,@AuthUser UserVo authUser) {
		//0. 포스트가 존재할 경우, FK 때문에 바로 삭제되지 않아서 이를 처리할 조건이 필요함.
		int count = blogService.categoryPostCount(no);
		System.out.println(">>Post 수 = "+count);
		if(count!=0) {
			//1. 해당 카테고리에 포스트가 존재할때 -> 미분류로 이동
			blogService.changeCategoryPosts(no,authUser.getId());
			blogService.deleteCategory(no);
		}else {			
			//2. 해당 카테고리에 포스트가 존재하지 않을때
			blogService.deleteCategory(no);
		}
		
		return "redirect:/blog/admin/category";
	}
	
	@Auth
	@RequestMapping(value="/admin/category/add",method=RequestMethod.POST)
	public String addCategory(@AuthUser UserVo authUser,	
			String name,
			 String description			
			) {
		CategoryVo cVo = new CategoryVo();
		cVo.setName(name);
		cVo.setBlogId(authUser.getId());
		cVo.setDescription(description);
		System.out.println(">>추가할 CategoryVo = "+cVo);
		blogService.insertCategory(cVo);

		return "redirect:/blog/admin/category";
	}
	
	@Auth
	@RequestMapping("/admin/write")
	public String adminWrite(@AuthUser UserVo authUser,
			@PathVariable("id") String blogId, Model model) {
		BlogVo blogVo = blogService.getBlogById(blogId);
		model.addAttribute("blogVo",blogVo);
		List<Map<String, Object>> cVo = blogService.getCategoryById(authUser.getId());
		model.addAttribute("list",cVo);
		return "blog/admin-write";
	}
	
	@Auth
	@RequestMapping(value="/admin/write",method=RequestMethod.POST)
	public String addWrite(@AuthUser UserVo authUser,	
			String title, Long category, String content		
			) {
		PostVo pVo = new PostVo();
		pVo.setCategoryNo(category);
		pVo.setContents(content);
		pVo.setTitle(title);
		
		System.out.println(">>추가할 postVo = "+pVo);
		blogService.insertPost(pVo);

		return "redirect:/"+authUser.getId();
	}
}
