// JavaScript Document
var OpenItem = '-2';
function getQueryString(name)
{
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}
function HttpPost(url,sendData){
	var result = null;
	jQuery.ajax({  
		type: "POST",
		cache: false,
		url: url,
		data: sendData, 
		async: false,
		dataType: "text",
		success: function(data) {  
			result = data;
		},
		error: function (XMLHttpRequest, textStatus, errorThrown) 
		{ 
			result = 'error';
		} 
	});
	return result;
}
function SetMenu(){
	setVersionContent();
	GetMenu(-2);
}
function GetMenu(xh){
	var result = HttpPost('/Wiki_getGroup.action','{}');
	var result1 = HttpPost('/Wiki_getAction.action','{}');
	if(result == 'error' || result1 == 'error'){
		alert('获取分组失败!');
		return;
	}
	var json = JSON.parse(result);
	var json1 = JSON.parse(result1);
	if(json.Result != "10000" || json1.Result != '10000'){
		alert("获取数据失败!")
		return;
	}
	for(i in json.Body){
		$('.apileft').append('<h2 id="h_'+i+'"><a id="title_'+i+'" href="javascript:lookAll(' + i + ')" class=" item">'+json.Body[i].Name+'</a></h2>');
		$('#h_'+i).append('<ul id="ul_'+i+'" class="sub-nav" style="display: none;"></ul>');
	}
	$('.apileft').append('<h2 class="cos" id="h_-1"><a id="title_-1" href="javascript:lookAll(-1)" class=" item">未分类接口</a></h2>');
	$('#h_-1').append('<ul id="ul_-1" class="sub-nav" style="display: block;"></ul>');
	for(i in json1.Body){
		if(xh == i){
			$('#h_'+json1.Body[i].GroupID).attr('class','cos');
			$('#ul_'+json1.Body[i].GroupID).css('display','block');
			$('#title_'+json1.Body[i].GroupID).attr('class',' active item');
			$('#h_'+OpenItem).attr('class','')
			$('#ul_'+OpenItem).css('display','none');
			$('#title_'+OpenItem).attr('class',' item');
			OpenItem = json1.Body[i].GroupID;
			$('#ul_'+json1.Body[i].GroupID).append('<li><a class="active item" id="a_' + i + '" href="?x='+i+'&action=' + json1.Body[i].Action + '">'+json1.Body[i].Name+'</a></li>');
		}else
			$('#ul_'+json1.Body[i].GroupID).append('<li><a id="a_' + i + '" href="?x='+i+'&action=' + json1.Body[i].Action + '">'+json1.Body[i].Name+'</a></li>')
	}
	if(!($('#h_-1').find('li').lenth > 0)){
		$('#h_-1').css('display','none');
	}
	
}
function lookAll(item){
	if(OpenItem != item){
		$('#h_'+item).attr('class','cos');
		$('#ul_'+item).css('display','block');
		$('#title_'+item).attr('class',' active item');
		$('#h_'+OpenItem).attr('class','')
		$('#ul_'+OpenItem).css('display','none');
		$('#title_'+OpenItem).attr('class',' item');
		OpenItem = item;
		if(item != '-2'){
			setWikiContent();
			$('#ul_'+item).find('a')[0].click();
		}else{
			setVersionContent();
		}
	}
}
function lookInfo(action){
	var result = HttpPost('/Wiki_getActionInfo.action','{"Name":"'+action+'"}');
	if(result == 'error'){
		alert('获取分组失败!');
		return;
	}
	var json = JSON.parse(result);
	if(json.Result != '10000'){
		alert('获取数据失败!');
	}
	$('#'+action).attr('class',' active item');
	$('#action_name').text(json.Body.Name);
	$('#action_desc').text(json.Body.Desc);
	$('#action_method').text('访问方式:'+json.Body.Method);
	$('#action_url').text('URL: '+json.Body.Url);
	$('#action_type').text(''+json.Body.Type);
	$('#action_powerid').text(''+json.Body.PowerID);
	$('#action_edit').text(''+json.Body.LastTime);
	$('#action_author').text(''+json.Body.Author);
	$('#action_power').text(''+json.Body.Power);
	if(json.Body.Power == "限制接口"){
		$('#Limit_Value').text(json.Body.Limit);
	}else{
		$('#Limit').remove();
	}
	$('#action_request').html(JSON.stringify(json.Body.RequestExample,null,'\t'))
	$('#action_returnexample').html(JSON.stringify(json.Body.ReturnExample,null,'\t'))
	$('#action_param').html('<tr><th width="120px">参数名</th><th width="100px">类型</th><th width="30px">必须</th><th>描述</th><th>示例</th></tr>');
	for(i in json.Body.Param){
		$('#action_param').append(json.Body.Param[i].htmlTR);
	}
	$('#action_return').html('<tr><th width="120px">参数名</th><th width="100px">类型</th><th width="30px">必须</th><th>描述</th><th>示例</th></tr>');
	for(i in json.Body.Return){
		$('#action_return').append(json.Body.Return[i].htmlTR);
	}
}
function setWikiContent(){
	var temp_str = '<h2 id="action_name" class="page-title">{name}</h2>'
	+ '        <article class="doc">'
	+ '            <section class="one"><br />'
	+ '            	<p id="action_desc"></p><br />'
	+ '                <code id="action_url">'
	+ '                </code><br /><br />'
	+ '                <p id="action_method"></p><br />'
	+ '                <p>属性：</p>'
	+ '                <div class="can">'
	+ '                    <table>'
	+ '                        <tbody><tr>'
	+ '                            <th>名称</th>'
	+ '                            <th>属性值</th>'
	+ '                            <th>说明</th>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>接口作者</td>'
	+ '                            <td id="action_author">JSON</td>'
	+ '                            <td>接口开发者</td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>数据格式</td>'
	+ '                            <td id="action_type">JSON</td>'
	+ '                            <td>调用接口发送的数据格式</td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>接口权限</td>'
	+ '                            <td id="action_power">普通接口</td>'
	+ '                            <td>如果为普通接口，则不需要权限</td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>权限序号</td>'
	+ '                            <td id="action_powerid">0</td>'
	+ '                            <td>接口权限序列</td>'
	+ '                        </tr>'
	+ '                        <tr id="Limit">'
	+ '                            <td>两次访问间隔</td>'
	+ '                            <td id="Limit_Value"></td>'
	+ '                            <td>访问该接口最小时间</td>'
	+ '                        </tr>'
	+ '                        <tr>'
	+ '                            <td>修改时间</td>'
	+ '                            <td id="action_edit">0</td>'
	+ '                            <td>接口最后修改时间</td>'
	+ '                        </tr>'
	+ '                    </tbody></table>'
	+ '                </div><br />'
	+ '                <p>参数：</p>'
	+ '                <div class="can">'
	+ '                    <table>'
	+ '                        <tbody id="action_param">'
	+ '                    </tbody></table>'
	+ '                </div><br />'
	+ '                <p>返回：</p>'
	+ '                <div class="can">'
	+ '                    <table>'
	+ '                        <tbody id="action_return">'
	+ '                    </tbody></table>'
	+ '                </div><br />'
	+ '                <p>请求示例</p>'
	+ '                <pre>'
	+ '                	<code id="action_request">{code}</code>'
	+ '				</pre>'
	+ '				<p>返回示例</p>'
	+ '                <pre>'
	+ '                	<code id="action_returnexample">{code}</code>'
	+ '				</pre>'
	+ '            </section>'
	+ '        </article>';
	$('#content').html(temp_str);
}
function setVersionContent(){
	var result = HttpPost('/Wiki_getPlatform.action','{}');
	if(result == 'error'){
		alert('接口初始化失败!');
		return;
	}
	var json = JSON.parse(result);
	if(json.Result != '10000'){
		alert('获取平台数据失败!');
	}
	
	var temp_str = '<h2 class="page-title">框架介绍</h2><article class="doc">'
		+ '<section class="one">'
		+ '<h3>1.注意事项</h3>'
		+ '<p>为保障数据能够被服务器正常解析,请务必注意以下内容</p>'
		+ '<p>1) Body为JSONObject以UTF-8 Base64进行编码，并参与签名</p>'
		+ '<p>2) 请认真查看各个参数数据类型</p>'
		+ '<p>3) 文档中 Type_ 开头代表参数类型</p>'
		+ '<p>4) 所有键值对,值必须强制转换成字符串后存入JSON</p>'
		+ '<p>5) 除非接口有特殊说明,否则不要传递特殊字符</p>'
		+ '<p>6) Type_MD5参数均为小写,切勿传递大写MD5</p>'
		+ '<p>7) Sign签名字段建议大写</p>'
		+ '<p>8) 所有返回参数中是否必须为true的，是在接口调用成功前提下</p>'

		+ '<h3>2.请求报文格式</h3>'
+'<pre><code>'
+'{'
+'\n	"Head": "",		-- 平台标示号'
+'\n	"Body": { },		-- 数据内容,必须为JSONobejct对象经过UTF-8 Base64编码字符串'
+'\n	"Sign": "",		-- 将数据内容用签名算法计算后存储到该键内'
+'\n	"Timestamp": "",	-- 时间。 可用 System.currentTimeMillis() 获取'
+'\n				   此外改时间必须与服务器接收到的时间间隔'
+'\n				   小于10s，如果大于10s会被拒收'
+'\n	"Session": ""		-- 如果接口有权限要求,必须有该键名'
+'\n}'
+'</code></pre>'
		+ '<h3>3.回复报文格式</h3>'
+'<pre><code>'
+'{'
+'\n	"Result": "",		-- 结果代码 非 10000 全为失败'
+'\n	"Body": { },		-- 数据内容,必须为JSONobejct'
+'\n	"Sign": "",		-- 请务必检验签名是否正确'
+'\n	"Timestamp": "",	-- 回复时间。 System.currentTimeMillis() 获取'
+'\n	"Error_Msg": ""		-- 返回代码非 10000 部分代码会返回该键值'
+'\n				   用于详细的描述错误信息'
+'\n}'
+'</code></pre>'
		+ '</section>'
		+ '</article></br></br>';
	temp_str += '<h2 class="page-title">框架信息</h2><article class="doc"><section class="one">'
		+ '                <p>平台密钥：</p>'
		+ '                <div class="can">'
		+ '                    <table><tbody id="Nightcat_p"><tr>'
		+ '                            <th>标识号</th>'
		+ '                            <th>平台名</th>'
		+ '                            <th>密钥</th>'
		+ '                        </tr>'
		+ '                    </tbody></table>'
		+ '                </div><br /><pre></code>'
		+ HttpPost('/Update.txt','')
		+ '</code></pre></section></article>';
	$('#content').html(temp_str);
	$('#Nightcat_p').append(json.Body.htmlTR);
}