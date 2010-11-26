<button type="button" loxiaType="button" <#rt/>
<#if parameters.buttonType?exists>
 buttonType="${parameters.buttonType?html}"<#rt/>
</#if>
<#if parameters.name?exists>
 name="${parameters.name?html}"<#rt/>
</#if>
 <#if parameters.nameValue?exists>
 value="<@s.text name="${parameters.nameValue?html}"/>"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.href?exists>
 href="${parameters.href?html}"<#rt/>
</#if>
<#if parameters.target?exists>
 target="${parameters.target?html}"<#rt/>
</#if>
<#if parameters.popfor?exists>
 htmlfor="${parameters.popfor?html}"<#rt/>
</#if>
<#if parameters.htmlAttr?exists>
 ${parameters.htmlAttr?html}<#rt/>
</#if>
<#if parameters.tabindex?exists>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
>