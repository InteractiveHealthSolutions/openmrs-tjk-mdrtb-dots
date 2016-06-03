package org.openmrs.module.mdrtb.extension.html;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.mdrtb.MdrtbConstants;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.util.StringUtils;

@Deprecated
public class RedirectSomeRolesFromOpenmrsHomepage extends Extension {

    /**
     * @see org.openmrs.module.Extension#getMediaType()
     */
    @Override
    public MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }

    /**
     * @see org.openmrs.module.Extension#getOverrideContent(java.lang.String)
     * 
     * @should do nothing if no user is logged in
     * @should do nothing if the ROLES_TO_REDIRECT global property is not set
     * @should do nothing if none of the authenticated users roles are in the ROLES_TO_REDIRECT global property
     * @should redirect the user via javascript if one of the authenticated users roles is in the ROLES_TO_REDIRECT global property 
     */
    @Override
    public String getOverrideContent(String str){
        if (!Context.isAuthenticated()) {
            return "";
        }
        Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES);
        try {
            String roleNamesToRedirect = Context.getAdministrationService().getGlobalProperty(MdrtbConstants.ROLES_TO_REDIRECT_GLOBAL_PROPERTY);
            if (!StringUtils.hasText(roleNamesToRedirect)) {
                return "<BR><BR><BR><Br><center><br><BR><image ><img src='/openmrs/images/openmrs_logo_large.gif' alt='OpenMRS'><center>";
            }
            String[] temp = roleNamesToRedirect.split(",");
            Set<String> set = new HashSet<String>(Arrays.asList(temp));
            for (Role role : Context.getAuthenticatedUser().getAllRoles()) {
                if (set.contains(role.getRole())) {
                    return "<script type=\"text/javascript\"> window.location = 'module/mdrtb/mdrtbIndex.form'; </script>";
                }
            }
        } finally {
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES);
        }
        return "<BR><BR><BR><Br><center><br><BR><image ><img src='/openmrs/images/openmrs_logo_large.gif' alt='OpenMRS'><center>";        
    }

}
