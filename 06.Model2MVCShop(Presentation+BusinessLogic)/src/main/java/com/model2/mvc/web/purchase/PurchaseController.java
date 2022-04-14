package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;

public class PurchaseController {

	// field
	@Autowired
	@Qualifier("productServiceImpl")
	private PurchaseService purchaseService;

	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;


	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;

	@Value("#{commonProperties['pageSize']}")
	int pageSize;

	// ctor
	public PurchaseController() {
		System.out.println(this.getClass());
	}


	// method
	@RequestMapping("/addPurchaseView.do")
	public ModelAndView addPurchaseView(@RequestParam("prodNo") int prodNo, HttpSession session) throws Exception {

		System.out.println("/addPurchaseView.do");

		User user = (User) session.getAttribute("user");

		Product product = new Product();
		product = productService.getProduct(prodNo);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/addPurchaseView.jsp");
		modelAndView.addObject("user", user);
		modelAndView.addObject("product", product);

		return modelAndView;
	}

	@RequestMapping(value="/addPurchse.do", method=RequestMethod.POST)
	public ModelAndView addPurchase(@ModelAttribute("purchase") Purchase purchase, @ModelAttribute("product") Product product) throws Exception {

		System.out.println("/addPurchase.do");

		purchaseService.addPurchase(purchase);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/purchase/addPurchase.jsp");
		modelAndView.addObject("purchase", purchase);

		return modelAndView;
	}


	@RequestMapping(value="/getPurchase.do")
	public ModelAndView getPurchase(@RequestParam("tranNo") int tranNo) throws Exception {

		System.out.println("/getPurchase.do");

		Purchase purchase = purchaseService.getPurchase(tranNo);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/getPurchase.jsp");
		modelAndView.addObject("purchase", purchase);

		return modelAndView;

	}


	@RequestMapping("/listPurchase.do")
	public ModelAndView listPurchase( @ModelAttribute("search") Search search, @RequestParam("menu") String menu, HttpSession session) throws Exception {

		System.out.println("/listPurchase.do");

		User user = (User) session.getAttribute("user");

		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);

		Map<String, Object> map = purchaseService.getPurchaseList(search, user.getUserId());
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/getPurchase.jsp");
		modelAndView.addObject("list", map.get("list"));
		modelAndView.addObject("resultPage", resultPage);
		modelAndView.addObject("search", search);
		modelAndView.addObject("menu", menu);

		return modelAndView;		
	}


	@RequestMapping("/listSale.do")
	public ModelAndView listSale(@ModelAttribute("search") Search search, @RequestParam("menu") String menu, HttpSession session) throws Exception {

		System.out.println("/listSale.do");

		String userId = ((User) session.getAttribute("user")).getUserId();

		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String, Object> map = purchaseService.getUserSaleList(search, userId);
		
		if(userId.equals("admin")) {
			map = purchaseService.getSaleList(search);
		} 
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/getPurchase.jsp");
		modelAndView.addObject("list", map.get("list"));
		modelAndView.addObject("resultPage", resultPage);
		modelAndView.addObject("search", search);
		
		return modelAndView;

	}

	
	@RequestMapping("/updatePurchaseView.do")
	public ModelAndView updatePurchaseView( @RequestParam("tranNo") int tranNo ) throws Exception {
		
		System.out.println("/updatePurchaseView.do");
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/updatePurchaseView.jsp");
		modelAndView.addObject("purchase", purchase);
		
		return modelAndView;
		
	}

	
	@RequestMapping("/updatePurchase.do")
	public ModelAndView updatePurchase( @ModelAttribute("purchase") Purchase purchase ) throws Exception {
		
		System.out.println("/updatePurchase.do");
		
		purchaseService.updatePurchase(purchase);
		
		purchase = purchaseService.getPurchase(purchase.getTranNo());
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/getPurchase.do?tranNo="+purchase.getTranNo());
		
		return modelAndView;
	}

	
	@RequestMapping("/updateTranCodeByProdAction.do")
	public ModelAndView updateTranCodeByProd(@RequestParam("tranNo") int tranNo, @RequestParam("tranCode") String tranCode, @RequestParam("menu") String menu) throws Exception {
		
		System.out.println("/updateTranCodeByProdAction.do");
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		purchase.setTranCode(tranCode);
		
		purchaseService.updateTranCode(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/listSale.do?menu=manage");
	
		return modelAndView;
	}

}