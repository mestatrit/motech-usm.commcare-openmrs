package org.motechproject.admin.web.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.admin.bundles.BundleIcon;
import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.admin.service.ModuleAdminService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.server.osgi.BundleInformation;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class BundleAdminController {

    @Autowired
    private ModuleAdminService moduleAdminService;

    @Autowired
    private StatusMessageService statusMessageService;

    @RequestMapping(value = "/bundles", method = RequestMethod.GET)
    @ResponseBody public List<BundleInformation> getBundles() {
        return moduleAdminService.getBundles();
    }

    @RequestMapping(value = "/bundles/{bundleId}", method = RequestMethod.GET)
    @ResponseBody public BundleInformation getBundle(@PathVariable long bundleId) {
        return moduleAdminService.getBundleInfo(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/detail")
    @ResponseBody public ExtendedBundleInformation getBundleDetails(@PathVariable long bundleId) {
        return moduleAdminService.getBundleDetails(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/start", method = RequestMethod.POST)
    @ResponseBody public BundleInformation startBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.startBundle(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/stop", method = RequestMethod.POST)
    @ResponseBody public BundleInformation stopBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.stopBundle(bundleId);
    }

    @RequestMapping(value = "/bundles/{bundleId}/restart", method = RequestMethod.POST)
    @ResponseBody public BundleInformation restartBundle(@PathVariable long bundleId) throws BundleException {
        return moduleAdminService.restartBundle(bundleId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/bundles/{bundleId}/uninstall", method = RequestMethod.POST)
    public void uninstallBundle(@PathVariable long bundleId) throws BundleException {
        moduleAdminService.uninstallBundle(bundleId);
        statusMessageService.ok("{bundles.uninstall.success}");
    }

    @RequestMapping(value = "/bundles/upload", method = RequestMethod.POST)
    @ResponseBody public BundleInformation uploadBundle(@RequestParam MultipartFile bundleFile,
                                                        @RequestParam(required = false) String startBundle) {
        boolean start = (StringUtils.isBlank(startBundle) ?  false : "on".equals(startBundle));
        return moduleAdminService.installBundle(bundleFile, start);
    }

    @RequestMapping(value = "/bundles/{bundleId}/icon", method = RequestMethod.GET)
    public void getBundleIcon(@PathVariable long bundleId, HttpServletResponse response) throws IOException {
        BundleIcon bundleIcon = moduleAdminService.getBundleIcon(bundleId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(bundleIcon.getContentLength());
        response.setContentType(bundleIcon.getMime());

        response.getOutputStream().write(bundleIcon.getIcon());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BundleException.class)
    public void handleBundleException(BundleException ex) {
        Throwable rootEx = (ex.getCause() == null ? ex : ex.getCause());
        statusMessageService.error(rootEx.getMessage());
    }
}
