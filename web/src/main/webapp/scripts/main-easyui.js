function addTab(node){
	$("#tabs-nav").tabs('add',{
		title:node.text,
		content:'Tab Body ',
		iconCls:'icon-save',
		closable:true,
		href:node.attributes.href
	});

}

$(function(){
	$('#tabs-nav').tabs({
	});
			$('#menu-tree-1').tree({
				//checkbox: true,
				url: 'tree_data.json',
				onClick:function(node){
					$(this).tree('toggle', node.target);
					//alert('you dbclick '+node.text);
					var node = $("#menu-tree-1").tree('getSelected');
					
					var b = $("#menu-tree-1").tree('isLeaf', node.target);
					if(b){
						//opentab
						addTab(node);
						}
				},
				onContextMenu: function(e, node){
					e.preventDefault();
					$('#tt2').tree('select', node.target);
					$('#mm').menu('show', {
						left: e.pageX,
						top: e.pageY
					});
				}
			});
});