package se.vgregion.ifeed.controller;

import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.FieldsInf;

@Controller
@RequestMapping("VIEW")
@SessionAttributes({ "fields" })
public class AdminFieldsController {

    @Autowired
    private IFeedService iFeedService;

    @ModelAttribute("fields")
    public FieldsInf getCommandObject() {
        System.out.println("getCommandObject() " + iFeedService);
        List<FieldsInf> infs = iFeedService.getFieldsInfs();
        if (infs.isEmpty()) {
            return new FieldsInf();
        }
        return infs.get(infs.size() - 1);
    }

    @RenderMapping(params = { "view=showAdminFields" })
    public String rm(final RenderResponse response) {
        System.out.println("rm");
        return "adminFields";
    }

    @ActionMapping(params = { "action=adminFields" })
    public void am(final ActionResponse response, final SessionStatus sessionStatus, final Model model) {
        System.out.println("am");
        response.setRenderParameter("view", "showAdminFields");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @ActionMapping(params = { "action=addFieldConfig" })
    public void save(@ModelAttribute(value = "fields") FieldsInf fields, final ActionResponse response,
            final SessionStatus sessionStatus, final Model model) {
        System.out.println("save " + fields);
        iFeedService.storeFieldsInf(fields);
        EditIFeedController.setFieldInfsCache(null);
        response.setRenderParameter("view", "showAdminFields");
    }

}