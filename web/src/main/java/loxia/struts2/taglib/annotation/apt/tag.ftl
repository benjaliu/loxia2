<!--
This file is generated during the build by processing Component class annotations.
Please do not edit it directly.
-->
<html>
    <head>
		<title>${tag.name}</title>
	</head>

	<body>
		<h1>Tag Name: ${tag.name}</h1>
		<h2>Description</h2>
		<p>
		<!-- START SNIPPET: tagdescription -->
		${tag.description}
		<!-- END SNIPPET: tagdescription -->
		</p>

		<h2>Attributes</h2>
		<!-- START SNIPPET: tagattributes -->
		<table width="100%">
			<tr>
				<th align="left" valign="top"><h4>Name</h4></th>
				<th align="left" valign="top"><h4>Required</h4></th>
				<th align="left" valign="top"><h4>Default</h4></th>
				<th align="left" valign="top"><h4>Evaluated</h4></th>
				<th align="left" valign="top"><h4>Type</h4></th>
				<th align="left" valign="top"><h4>Description</h4></th>
			</tr>
			<#list tag.attributes as att>
				<tr>
					<td align="left" valign="top">${att.name}</td>
					<td align="left" valign="top"><#if att.required><strong>true</strong><#else>false</#if></td>
					<td align="left" valign="top">${att.defaultValue}</td>
					<td align="left" valign="top">${att.rtexprvalue?string}</td>
					<td align="left" valign="top">${att.type}</td>
					<td align="left" valign="top">${att.description}</td>
				</tr>
			</#list>
		</table>
		<!-- END SNIPPET: tagattributes -->
	</body>
</html>

