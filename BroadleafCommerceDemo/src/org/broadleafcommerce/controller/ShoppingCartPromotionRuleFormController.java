package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class ShoppingCartPromotionRuleFormController extends SimpleFormController {
	/*
	protected final Log logger = LogFactory.getLog(getClass());

	private RuleService ruleService;

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	protected Object formBackingObject(HttpServletRequest request)
			throws ServletException {

		ShoppingCartPromotion shoppingCartPromotion = new ShoppingCartPromotion();

		return shoppingCartPromotion;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		ShoppingCartPromotion shoppingCartPromotion = (ShoppingCartPromotion) command;

		ruleService.writeRuleFile(shoppingCartPromotion, request.getParameter("logicalOperator"));

		if (errors.hasErrors()) {
			logger.debug("Error returning back to the form");

			return showForm(request, response, errors);
		}

		ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());
		mav.addObject("saved", true);

		return mav;
	}
	*/
}
