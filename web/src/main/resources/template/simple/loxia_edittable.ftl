<#setting number_format="#"/>
<table loxiaType="table" settings="${parameters.settings?html}"<#rt/>
<#if parameters.cellpadding?exists>
 cellpadding="${parameters.cellpadding?html}"<#rt/>
</#if>
<#if parameters.cellspacing?exists>
 cellspacing="${parameters.cellspacing?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.htmlAttr?exists>
 ${parameters.htmlAttr?html}<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
>