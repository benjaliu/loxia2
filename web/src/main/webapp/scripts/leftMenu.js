var data=[
		{"id":"01","href":"#","description":"菜单1",
			"nodes":[
				{"id":"0101","isLeaf":true,"href":"frame/op-create-opb.html","description":"创建仓库作业单"},
				{"id":"0102","isLeaf":true,"href":"template-1.2.html","description":"功能2"},
				{"id":"0103","isLeaf":false,"href":"#","description":"功能3",
					"nodes":[
						{"id":"010301","isLeaf":true,"href":"#","description":"功能3.1"},
						{"id":"010302","isLeaf":true,"href":"#","description":"功能3.2"},
						{"id":"010303","isLeaf":false,"href":"#","description":"功能3.3",
							"nodes":[
								{"id":"01030301","isLeaf":true,"href":"#","description":"功能3.3.1"},
								{"id":"01030302","isLeaf":true,"href":"#","description":"功能3.3.2"},
								{"id":"01030303","isLeaf":true,"href":"#","description":"功能3.3.3"}
							]
						}
					]
				},
				{"id":"0104","isLeaf":true,"href":"www.baidu.com","description":"04菜单"},
				{"id":"0105","isLeaf":true,"href":"www.baidu.com","description":"05菜单"}
				]
		},
		{"id":"02","isEmpty":true,"href":"www.baidu.com","description":"菜单2"},
		{"id":"03","isEmpty":true,"href":"www.baidu.com","description":"菜单3"},
		{"id":"04","isEmpty":true,"href":"www.baidu.com","description":"菜单4"},
		{"id":"05","isEmpty":true,"href":"www.baidu.com","description":"菜单5"}];
function createTree(target,data){
	var menu=[];
	for(var i in data){
		menu.push(createMenu(data[i]));
	}
	jQuery("#"+target).append(menu.join(""));
}

function createMenu(menu){
	var html=[];
	appendHeadTag(html,"h3");
	appendA(html,menu.href,menu.description);
	appendTailTag(html,"h3");
	appendHeadTag(html,"div");
	if("true"==menu.isEmpty){
		html.push(menu.description);
		
	}else{
		html.push(createNode(menu.nodes));
	}
	appendTailTag(html,"div");
	return html.join("");
}
function createNode(nodes){
	var html=[];
	appendHeadTag(html,"ul");
	if(nodes){
		for(var i in nodes){
			appendHeadTag(html,"li");
			if(true==nodes[i].isLeaf){
				appendA(html,nodes[i].href,nodes[i].description);
			}else{
				appendHeadTag(html,"dl");
				appendHeadTag(html,"dt");
				appendA(html,nodes[i].href,nodes[i].description);
				appendHeadTag(html,"dd");
				html.push(createNode(nodes[i].nodes));
				appendTailTag(html,"dd");
				appendTailTag(html,"dl");
			}
			appendTailTag(html,"li");
		}
	}
	
	appendTailTag(html,"ul");
	return html.join("");
}
/*
 * append the tag to the target.
 */
function appendHeadTag(target,tag){
	target.push("<"+tag+">");
}
function appendTailTag(target,tag){
	target.push("</"+tag+">");
}
function appendA(target,href,desc){
	target.push('<a href="'+href+'">'+desc+'</a>');
}