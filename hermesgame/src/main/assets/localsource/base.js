
var myApp = new Framework7();
var $ = Dom7;
var mainView = myApp.addView('#mainView',{
	domCache: true,
	dynamicNavbar:true
});
var gameCardView = myApp.addView('#gameCardView',{
	domCache: true,
	dynamicNavbar:true
});
var noticeView = myApp.addView('#noticeView',{
	domCache: true,
	dynamicNavbar:true
});
var serviceView = myApp.addView('#serviceView',{
	domCache: true,
	dynamicNavbar:true
});
var bbsView = myApp.addView('#bbsView',{
	domCache: true,
	dynamicNavbar:true
});

var startY,clientY = 0;
var touchMoveListen = document.getElementById("touchMoveListen");
var header = document.getElementById('headers');
var imag = document.getElementById("changeImages");
var ids = document.getElementById("show_uid");
var beginTops = 0;
touchMoveListen.addEventListener('touchstart',function (event) {
    var touch = event.touches[0];
    startY = touch.pageY;
    clientY = touch.clientY;  
	beginTops = $(touchMoveListen).offset().top;
});

touchMoveListen.addEventListener('touchmove',function  (event) {
	if(beginTops != 135){
		return;
	}
    var touchs = event.touches[0];
    if (touchs.pageY - startY <= 0 ) {
        return ;
    }
    if(startY != clientY){
        return ;
    }
	if(((touchs.pageY - startY)/2) >= 70){
		return;
	}
    header.style.height = 170  + ((touchs.pageY - startY)/2) +'px';
	$("#changeImages").css('margin-top',25 + ((touchs.pageY - startY)/4) +'px');
    var scale = (touchs.pageY - startY ) / 170 + 1.0;
    header.style.transform = "scale("+ scale +","+ scale +")";
});
touchMoveListen.addEventListener('touchend',function  (event) {
    event.preventDefault();
    var touch = event.changedTouches[0];    
    var header = document.getElementById('headers');
    header.style.height = 170 +'px';     
	$("#changeImages").css('margin-top','25px');
    header.style.transform = "none";
});

function resetScreenOrientation(type){
	doActionFunction('resetScreenOrientation',{screenOrientation:type,windowRate:1});
}
//获取url参数链接中某个参数值
function getQueryVariable(query,variable)
{
	try{
		var vars = query.split("&");
	}catch(e){
		var vars =[];
	}
	for (var i=0;i<vars.length;i++) {
       var pair = vars[i].split("=");
       if(pair[0] == variable){return pair[1];}
	}
	return "";
}


var nowView = 'mainView';
$(document).on('click','.showPage',function(){
	var pageId = $(this).attr('pageId'), jumpUrl = $(this).attr('url');
	
	if(hasOpenWebSite == 1){
		myApp.hideIndicator();
		$(".preloader-indicator-modal").css('margin-left','0px');
		checkResetScreenType();
		hasOpenWebSite = 0;
		websiteClose();
		return;
	}
	if($(this).find(".redpoint").length>0){
		$(this).find(".redpoint").removeClass("redpoint");
	}
	
	// 自定链接，在iframe中加载
	if(jumpUrl != null && jumpUrl != void 0 && jumpUrl != ''){
		// 拼接userInfo
		var query = jumpUrl.split('?'), split = '&'
		if(query.length < 2){
			split = '?';
			var siteType = '';
		}else{
			var siteType = getQueryVariable(query[1],"sitetype"); //是否来自SDK自带链接
		}
		jumpUrl = `${jumpUrl}${split}authToken=${userInfo.authToken}&deviceId=${userInfo.deviceId}&productCode=${productInfo.productCode}&channelCode=${userInfo.channelCode}&productId=${productInfo.id}&fromSite=usercenter`

		if(jumpUrl.indexOf('portrait') != -1){
			resetScreenOrientation(0);
		}

		autoOpenGame = 0;
		hasOpenWebSite = 1;
		$("#siteFrameLayer").css('left','0px');
		
		myApp.showIndicator();
		qgamePopupFrame("siteFrameLayer",siteType=='qgame'?0:1);
		setTimeout(function(){
			$('#siteFrame').attr('src', jumpUrl);
			//$("#siteFrameLayer").css('display','block');
		},400);
		
		
		if(jumpUrl.indexOf('userCenter') == -1){
			var iframe = document.getElementById('siteFrame');
			iframe.onload = function(){ 
				myApp.hideIndicator();
				//clearInterval(checkSiteOpen);
				$(".preloader-indicator-modal").css('margin-left','0px');
			};
		}
		

		/*checkSiteOpen = setTimeout(function(){
			//30秒后
			if($(".preloader-indicator-modal").length > 0){
				clearInterval(checkSiteOpen);
				checkSiteOpen = null;
				myApp.hideIndicator();
				$(".preloader-indicator-modal").css('margin-left','0px');
				websiteClose();
				checkResetScreenType();
				layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
			}
		},15000);
		*/
		$(".preloader-indicator-modal").css('margin-left','-25px');
		return true;
	}

	$(".view").removeClass('active');
	$("#" + pageId).addClass('active');
	$.each($(".leftIcon"),function(k,v){
		$("#" + v.id).removeClass(v.id+"On");
	})
	$("#" + pageId+"Icon").addClass(pageId +'IconOn');
	myApp.closeModal();
	if(pageId == 'mainView'){
		if(nowView == 'mainView'){
			doBackMainViews();
		}
	}
	
	if(pageId == 'gameCardView'){
		if($("#giftList").html() == ''){
			$("#giftList").html('<div class="centerLoading"><span class="preloader"></span></div>');
			loadGiftPage(1);
		}
		
		putEventData('用户中心:进入礼包界面');
	}
	if(pageId == 'noticeView'){
		if($("#noticeList").html() == ''){
			$("#noticeList").html('<div class="centerLoading"><span class="preloader"></span></div>');
			loadNoticePage(1);
		}
		if(nowView == 'noticeView'){
			noticeView.router.back();
		}
		
		putEventData('用户中心:进入公告界面');
	}
	if(pageId == 'bbsView'){
		if($("#bbsList").html() == ''){
			$("#bbsList").html('<div class="centerLoading"><span class="preloader"></span></div>');
			loadThread(1);
		}
		if(nowView == 'bbsView'){
			bbsView.router.back();
		}
		putEventData('用户中心:进入社区界面');
	}
	if(pageId == 'serviceView'){
		// 云客服
		var serviceType = parseInt($(this).attr('srvtype'));
		if(serviceType == 2){
			doActionFunction('showServiceIM',{});
		}
		
		
		if($(".qaListLoading").length > 0){
			initServicePage();
		}
		
		if(nowView == 'serviceView'){
			serviceView.router.back();
		}
		putEventData('用户中心:进入客服界面');
	}
	nowView = pageId;
})
function qgamePopupFrame(domId,isFullScreen){
	myApp.popup('#'+domId);
	if(isFullScreen == 1){
		$('#'+domId).addClass("show-back-bar");
	}else{
		$('#'+domId).removeClass("show-back-bar");
	}
}

function doBackMainViews(){
	$(".modal-overlay").removeClass('modal-overlay-visible');
	$(".popup-overlay").removeClass('modal-overlay-visible')
	return myApp.closeModal();	
}

var newsTitle = null;
var newsType = 0;
var newsId = 0;
$(document).on('click','.openNews',function(){
	var title = $(this).attr('title');
	newsTitle = title;
	newsType = $(this).attr('type');
	newsId = $(this).attr('aid');
	$("#messageTitle").html(title);
	$("#showNewsDetail").html('<div class="centerLoading"><div class="preloader"></div></div>');

	$(this).find('.msg-point').remove();
	noticeView.router.load({pageName: 'detail'});
})

var tid = 0;
var threadTitle = '';
$(document).on('click','.threadLink',function(){
	tid = $(this).attr('tid');
	threadTitle = $("#threadTitle" + tid).html();
	$("#threadTitle").html(threadTitle).show();
	$(".showContent").hide();
	$("#threadDetailLoading").show();
	bbsView.router.load({pageName: 'threadDetail'});
})

$(document).on('click','.openDialog',function(){
	var pageId = $(this).attr('pageId');
	if(screenType == 1){
		$(".popup").css('left','60px');
	}
	myApp.popup("#" + pageId);
	
	if(pageId == 'orderList'){
		putEventData('用户中心:进入充值记录');
	}
	if(pageId == 'showChangePassword'){
		putEventData('用户中心:进入修改密码');
	}
})

$(document).on('click','.closeDialog',function(){
	var pageId = $(this).attr('pageId');
	myApp.closeModal("#" + pageId);
})

$('#orderList').on('opened', function () {
	nowOrderPage = 1;
	loadOrderList(nowOrderPage);
});

$('#orderList').on('open', function () {
	$("#orderShowList").html('<div class="centerLoading"><div class="preloader"></div></div>');
});

$(document).on('pageAfterAnimation', '.page[data-page="threadDetail"]', function (e) {
	loadThreadDetail(tid,1);  
})

$(document).on('pageAfterAnimation', '.page[data-page="detail"]', function (e) {
	loadNewsDetail(newsType,newsId);
});

function checkClosePop(){
	
}
/*
$(document).on('click',"#selectAvatar",function(){
	var params = {type:'avatar',rate:50,maxSelectNum:1};
	doActionFunction('selectImage',params);
	myApp.showIndicator();
})
*/
var giftPull = $('.giftPull');
var giftIsLoad = false;
giftPull.on('refresh', function (e) {
	if(giftIsLoad) return;
	giftIsLoad = true;
	myApp.attachInfiniteScroll($('.giftPull'));
	loadGiftPage(1);
});

var noticePull = $('.noticePull');
var noticeIsLoad = false;
noticePull.on('refresh', function (e) {
	if(noticeIsLoad) return;
	noticeIsLoad = true;
	myApp.attachInfiniteScroll($('.noticePull'));
	loadNoticePage(1);
});

var bbsPull = $('.bbsPull');
var bbsIsLoad = false;
bbsPull.on('refresh', function (e) {
	if(bbsIsLoad) return;
	bbsIsLoad = true;
	myApp.attachInfiniteScroll($('.bbsPull'));
	loadThread(1);
});

var threadPull = $('.threadPull');
var detailIsLoad = false;
threadPull.on('refresh', function (e) {
	if(detailIsLoad) return;
	detailIsLoad = true;
	myApp.attachInfiniteScroll($('.threadPull'));
	var prePage = parseInt(nowThreadPage) - 1;
	if(prePage <= 0){
		prePage = 1;
	}
	loadThreadDetail(tid,prePage);
});
threadPull.on('infinite', function (e) {
	if(detailIsLoad) return;
	detailIsLoad = true;
	var nextPage = parseInt(nowThreadPage) + 1;
	loadThreadDetail(tid,nextPage);
})

var nowOrderPage = 1;
var orderPull = $('.orderPull');
var orderIsLoad = false;
orderPull.on('refresh', function (e) {
	if(orderIsLoad) return;
	orderIsLoad = true;
	myApp.attachInfiniteScroll($('.orderPull'));
	$("#orderListList").attr('page','1');
	loadOrderList(1);
});

orderPull.on('infinite', function (e) {
	if(orderIsLoad) return;
	orderIsLoad = true;
	var nextPage = $("#orderListList").attr('page');
	loadOrderList(nextPage);
})

$('.bbsPull').on('infinite', function () {
	if(bbsIsLoad) return;
	bbsIsLoad = true;
	var nextPage = $("#bbsList").attr('page');
	loadThread(nextPage);
})

//////
var postsFid = 0;
//回帖
$(document).on('click','#reply-thread',function(){
	postsFid = 0
	bbsView.router.load({pageName: 'replyThread'});
})
// 回复评论
$(document).on('click', '.reply-posts', function(){
	postsFid = $(this).attr('data-pid');
	bbsView.router.load({pageName: 'replyThread'});
})
$(document).on('pageAfterAnimation', '.page[data-page="replyThread"]', function (e) {

})

var imgArray = new Array();
$(document).on('click', '#post-reply', function (e) {
	var text = $('#reply-content').val(),
	params = {tid: tid,
		postsFid: postsFid,
		content: text,
		authToken: userInfo.authToken,
		deviceId: userInfo.deviceId,
		productCode:productInfo.productCode,
		channelCode:userInfo.channelCode,
		imgList: imgArray.join(',')
	}
	
	if(text.length <= 0){
		return layer.open({content: '请输入回复内容',skin: 'msg',time: 3,anim:false });
	}
	
	$("#post-reply").addClass('disabled');
	myApp.showIndicator();
	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: '/bbs/submitPost',
		data: params,
		success: function(response){
			imgArray = [];
			$("#reply-content").val('');
			$(".img-items").remove();
			$("#post-reply").removeClass('disabled');
			myApp.hideIndicator();
			bbsView.router.back();
		},
		timeout:900000,
		error: function(s){
			$("#post-reply").removeClass('disabled');
			myApp.hideIndicator();
			return layer.open({content: '发布失败网络超时',skin: 'msg',time: 3,anim:false });
		}
	})
})

//发帖
$(document).on('click','#post-thread',function(){
	// threadTitle = $("#threadTitle" + tid).html();
	// $("#threadTitle").html(threadTitle).show();
	// $(".showContent").hide();
	// $("#threadDetail").html('<div class="threadLoading"><div class="preloader"></div></div>');
	bbsView.router.load({pageName: 'postThread'});
})
$(document).on('pageAfterAnimation', '.page[data-page="postThread"]', function (e) {
	
})
$(document).on('click', '#submit-thread', function (e) {
	
	var data = {
		title: $('#thread-title').val(),
		text: $('#post-content').val(),
		caid: $('#caid').val(),
		gameid: productInfo.id,
		authToken: userInfo.authToken,
		deviceId: userInfo.deviceId,
		productCode: productInfo.productCode,
		channelCode: userInfo.channelCode,
		imgList: imgArray.join(',')
	}
	
	if(data.title.length <= 0){
		return layer.open({content: '请输入标题',skin: 'msg',time: 3,anim:false });
	}
	if(data.text.length <= 0){
		return layer.open({content: '请输入话题内容',skin: 'msg',time: 3,anim:false });
	}

	myApp.showIndicator();
	$("#submit-thread").addClass('disabled');
	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: '/bbs/submitThread',
		data: data,
		success: function(response){
			if(response.status == true){
				imgArray = []
				myApp.hideIndicator();
				bbsView.router.back();
				myApp.pullToRefreshTrigger('.bbsPull');
				$("#post-content").val('');
				$("#thread-title").val('');
				$(".thisAddImage").remove();
				$("#submit-thread").removeClass('disabled');
			}else{
				layer.open({content: response.error});
			}
		},
		timeout:900000,
		error: function(){
			$("#submit-thread").removeClass('disabled');
			myApp.hideIndicator();
			return layer.open({content: '发布失败网络超时',skin: 'msg',time: 3,anim:false });
		}
	})
})



$(document).on('click','.threadImages',function(){
	var img = $(this).parent().find('img');
	var index = $(this).attr('index');
	var thisPhoto = new Array();
	$.each(img,function(k,v){
		thisPhoto.push(v.src);
	})
	var myPhotoBrowserStandalone = myApp.photoBrowser({
		photos : thisPhoto,
	});
	myPhotoBrowserStandalone.open(index);
	$(".photo-browser-of").html('/');
	$(".photo-browser-close-link").find('span').html('关闭').show();
})


window.onload = function(){
	loadGiftPage(1);
	// loadNoticePage(1);
	initServicePage();
}

//////
var nowThreadPage = 1;
function loadThreadDetail(tid,page){
	$.ajax({
		type: 'POST',
		url: '/bbs/threadRead',
		//dataType: 'json',
		data:{format:'json',tid:tid,page:page,gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success: function(dataString){
			var data = JSON.parse(dataString);
			detailIsLoad = false;
			myApp.pullToRefreshDone();
			$("#threadDetailLoading").hide();
			var showHtml = data.data.thread.content;
			if(data.data.thread.imgList != undefined && data.data.thread.imgList.length > 0){
				showHtml += '<div class="imgHub">';
				var photos = new Array();
				for(i = 0 ; i < data.data.thread.imgList.length;i++){
					var thisVal = data.data.thread.imgList[i];
					showHtml += '<br><img index="'+i+'" class="threadImages" src="'+thisVal+'">';
				}
				showHtml += '</div>';
			}
			var postHtml = '';
			if(data.data.posts.length > 0){
				for(i = 0 ; i < data.data.posts.length;i++){
					
					var thisVal = data.data.posts[i], content = ''
					var  postImgHtml = '<div class="imgHub" style="margin-top:0">';
					if(thisVal.imgList != undefined && thisVal.imgList.length > 0){
						for(j = 0 ; j < thisVal.imgList.length ; j++){
							postImgHtml += '<br><img index="'+j+'" class="threadImages" src="'+thisVal.imgList[j]+'">';
						}
					}
					postImgHtml += '</div>';
					
					if(thisVal.contentType == 3){ 
						var cnt = thisVal.content, parentImgHtml = ''
						thisVal['dateSay'] = cnt.subs.dateSay
						if(cnt.hasOwnProperty('imgList') && cnt.imgList.length > 0){
							parentImgHtml = '<div class="imgHub" style="margin-top:0">'
							for(ii in cnt.imgList){
								parentImgHtml += '<img index="'+ii+'" class="threadImages" src="'+cnt.imgList[ii]+'" style="width:30%;'
												 + (ii != 0 ? 'margin-left:3%;' : '') + '">'
							}
							parentImgHtml += '</div>';
						}

						content = 
						'<div class="parent-posts" data-uid="' + cnt.parent.uid + '">'
						+ '<span class="threadInfoDate">@' + cnt.parent.nickName
						+ '&nbsp;' + cnt.parent.dateSay + '</span>'
						+ '<p>' + cnt.parent.content + '</p>'
						+ parentImgHtml
						+ '</div>'
						+ '<p>' + cnt.subs.content + '</p>'
					}else{
						content = thisVal.content
					}
					postHtml += '<div class="postUserInfo">'
								+'<div class="postNumsSay">'+thisVal['floor']+'楼</div>'
								+'<div class="submitAvatar"><img onerror="imgerrorfun();" src="'+thisVal['avatar']+'"></div>'
								+'<div class="showPostName">'+thisVal['nickName']+'</div>'
								+'<div class="postLikeInfo"><a href="javascript:void(0)" class="reply-posts replyPostButton" data-pid="'+thisVal['pid']+'"><i class="bbsIcon bbsPost" data-pid="'+thisVal['pid']+'"></i>回复</a><span class="bbsShowArea"><i class="bbsIcon bbsActiveTime"></i><span class="threadInfoDate">'+thisVal['dateSay']+'</span></span></div>'
							+'</div>'
							+'<div class="postContent">' + content + '</div>'
							+postImgHtml;
					console.log(postImgHtml);
				}
			}
			
			if(data.data.posts.length == 20){
				$("#threadPage").show();
			}
			if(page == 1){
				$("#submitUsername").html(data.data.thread.nickName);
				$("#threadInfoZan").html(data.data.thread.recommendAdd);
				$("#threadInfoDate").html(data.data.thread.dateSay);
				$("#threadUserAvatar").attr('src',data.data.thread.avatar);
				$(".showContent").show();
				$("#threadDetail").html(showHtml);
				$("#postList").html(postHtml);
				if(data.data.thread.zan == 1){
					$("#likeStatus").attr('hasLike','1');
				}else{
					$("#likeStatus").attr('hasLike','0');
				}
			}else{
				$("#postList").append(postHtml);
				if(postHtml == ''){
					$("#threadPage").hide();
					myApp.detachInfiniteScroll($(".threadList")); 
				}
			}
			
			nowThreadPage = page;
		},
		error:function(){
			detailIsLoad = false;
			myApp.pullToRefreshDone();
		}
	});
}

$(document).on('click','#showSelectIcon',function(){
	var buttons = [
        {
            text: '筛选订单',
            label: true,
        },
        {
            text: '全部订单',
            bold: true,
			color:'black',
			onClick: function () {
				myApp.attachInfiniteScroll($('.orderPull'));
				nowOrderPage = 1;
				orderWhere = 1;
				orderIsLoad = false;
				$("#orderShowList").html('');
				$("#orderLoadMore").show();
				$(".noOrderList").remove();
				loadOrderList(nowOrderPage);
				$("#showSelectIcon").removeClass('icon-select-1').removeClass('icon-select-2').removeClass('icon-select').addClass('icon-select');
			}
        },
        {
            text: '已完成订单',
			color:'black',
			onClick: function () {
				nowOrderPage = 1;
				orderWhere = 2;
				orderIsLoad = false;
				myApp.attachInfiniteScroll($('.orderPull'));
				$("#orderShowList").html('');
				$("#orderLoadMore").show();
				$(".noOrderList").remove();
				$("#showSelectIcon").removeClass('icon-select-1').removeClass('icon-select-2').removeClass('icon-select').addClass('icon-select-2');
				loadOrderList(nowOrderPage);
			}
        },
		{
            text: '异常订单',
			color:'black',
			onClick: function () {
				nowOrderPage = 1;
				orderWhere = 3;
				orderIsLoad = false;
				myApp.attachInfiniteScroll($('.orderPull'));
				$("#orderShowList").html('');
				$("#orderLoadMore").show();
				$(".noOrderList").remove();
				$("#showSelectIcon").removeClass('icon-select-1').removeClass('icon-select-2').removeClass('icon-select').addClass('icon-select-1');
				loadOrderList(nowOrderPage);
				
			}
        },
        {
            text: '关闭',
            color: 'red'
        },
    ];
    myApp.actions(buttons);
})


$(document).on('click','.toPage',function(){
	var pageId = $(this).attr('pageId');
	serviceView.router.load({pageName: pageId});
})

$(document).on('keyup','#searchKey',function(e){

	if('' == $(this).val()){
		$("#searchResult").hide();
		$("#searchTips").show();
		$(".clearSearchKey").css('display','none');
	}else{
		if(e.keyCode == 13){
			$(".searchButton").trigger('click');
		}
		$(".clearSearchKey").css('display','inline-block');
	}
})

$(document).on('click','.clearSearchKey',function(){
	$("#searchKey").val('');
	$("#searchResult").hide();
	$("#searchTips").show();
	$(this).hide();
})

$(document).on('click',".hotSearchItem",function(){
	var key = $(this).html();
	$("#searchKey").val(key);
	$(".clearSearchKey").css('display','inline-block');
	beginSearch();
})

$(document).on('click','.searchButton',function(){
	beginSearch();
})

$(document).on('click','.clearSearchLog',function(){
	localStorage.removeItem('searchList');
	$("#localSearch").html('');
	return layer.open({content: '搜索记录已清理',skin: 'msg',time: 2,anim:false });
})

var thisQATitle = '';
var thisQAContent = '';
$(document).on('click','.openQA',function(){
	thisQATitle = $(this).attr('title');
	thisQAContent = $(this).attr('qa');
	thisQAContent = thisQAContent.replace(/\|/g,"<br>");
	$("#qaDetailTitle").html(thisQATitle);
	$("#qaDetailContent").html(thisQAContent);
	
	$(".useFull").removeClass('useFullOn');
	$(".unUseFull").removeClass('unUseFullOn');
	
	var isUseFull = getUserFul(thisQATitle);
	if(isUseFull){
		$(".useFull").addClass('useFullOn');
		$(".unUseFull").removeClass('unUseFullOn');
	}
	
	var isUnUseFull = getUnUserFul(thisQATitle);
	if(isUnUseFull){
		$(".useFull").removeClass('useFullOn');
		$(".unUseFull").addClass('unUseFullOn');
	}
	
	serviceView.router.load({pageName: "qaDetail"});
})

$(document).on('click','.qaUsefull',function(){
	$(".useFull").addClass('useFullOn');
	$(".unUseFull").removeClass('unUseFullOn');
	addUserFul(thisQATitle);
	delUnUserFul(thisQATitle);
})

$(document).on('click','.qaUnUsefull',function(){
	$(".useFull").removeClass('useFullOn');
	$(".unUseFull").addClass('unUseFullOn');
	addUnUserFul(thisQATitle);
	delUserFul(thisQATitle);
})

function getUserFul(txt){
	var getSaveList = localStorage.getItem('qaUsefull');
	if(typeof(getSaveList) == 'string'){
		getSaveList = JSON.parse(getSaveList);
	}
	if(getSaveList != null && typeof(getSaveList) == 'object' && getSaveList.hasOwnProperty(txt)){
		return true;
	}
	return false;
}

function getUnUserFul(txt){
	var getSaveList = localStorage.getItem('qaUnUsefull');
	if(typeof(getSaveList) == 'string'){
		getSaveList = JSON.parse(getSaveList);
	}
	if(getSaveList != null && typeof(getSaveList) == 'object' && getSaveList.hasOwnProperty(txt)){
		return true;
	}
	return false;
}

function addUserFul(txt){
	var getSaveList = localStorage.getItem('qaUsefull');
	if(typeof(getSaveList) == 'string'){
		try{
			getSaveList = JSON.parse(getSaveList);
		}catch(e){
			getSaveList = new Object();
		}
	}
	
	if(getSaveList == null){
		getSaveList = new Object();
	}
	getSaveList[txt] = 1;
	localStorage.setItem('qaUsefull',JSON.stringify(getSaveList));
}
function addUnUserFul(txt){
	var getSaveList = localStorage.getItem('qaUnUsefull');
	if(typeof(getSaveList) == 'string'){
		try{
			getSaveList = JSON.parse(getSaveList);
		}catch(e){
			getSaveList = new Object();
		}
	}
	
	if(getSaveList == null){
		getSaveList = new Object();
	}
	getSaveList[txt] = 1;
	localStorage.setItem('qaUnUsefull',JSON.stringify(getSaveList));
}
function delUnUserFul(txt){
	var getSaveList = localStorage.getItem('qaUnUsefull');
	if(typeof(getSaveList) == 'string'){
		getSaveList = JSON.parse(getSaveList);
	}
	if(getSaveList != null && typeof(getSaveList) == 'object' && getSaveList.hasOwnProperty(txt)){
		delete(getSaveList[txt]);
	}
	localStorage.setItem('qaUnUsefull',JSON.stringify(getSaveList));
}
function delUserFul(txt){
	var getSaveList = localStorage.getItem('qaUsefull');
	if(typeof(getSaveList) == 'string'){
		getSaveList = JSON.parse(getSaveList);
	}
	if(getSaveList != null && typeof(getSaveList) == 'object' && getSaveList.hasOwnProperty(txt)){
		delete(getSaveList[txt]);
	}
	localStorage.setItem('qaUsefull',JSON.stringify(getSaveList));
}


$(document).on('click','.qaUsefull',function(){
	var getSaveList = localStorage.getItem('qaUsefull');
	console.log(getSaveList);
	if(getSaveList == null || getSaveList == '' || typeof(getSaveList) != 'string'){
		getSaveList = new Object();
	}
	try{
		getSaveList = JSON.parse(getSaveList);
	}catch(e){
		getSaveList = new Object();
	}
	getSaveList[thisQATitle] = 1;
	localStorage.setItem('qaUsefull',JSON.stringify(getSaveList));
	$(".useFull").addClass('useFullOn');
})
function beginSearch(){
	var keyword = $("#searchKey").val();
	if(keyword.length <= 0){
		$("#searchResult").hide();
		$("#searchTips").show();
		return;
	}
	$("#searchResult_loading").show();
	$("#searchResultListUl").html('');
	$("#searchResult").show();
	$("#searchTips").hide();
	var getSaveList = localStorage.getItem('searchList');
	if(getSaveList == null || getSaveList == '' || typeof(getSaveList) != 'string'){
		getSaveList = new Array();
	}
	try{
		getSaveList = JSON.parse(getSaveList);
	}catch(e){
		getSaveList = new Array();
	}
	
	if(getSaveList.length > 0 && getSaveList[0] == keyword){
	}else{
		
		for(i = 0 ; i < getSaveList.length ; i++){
			if(getSaveList[i] == keyword){
				getSaveList.splice(i,1);
			}
		}
		getSaveList.unshift(keyword);
		var saveList = getSaveList.slice(0,20);
		localStorage.setItem('searchList',JSON.stringify(saveList));
	}
	loadLocalSearchLogs();
	loadSearchResult(keyword,function(data){
		var html = '';
		if(data.data.length <= 0){
			$("#searchResult_loading").hide();
			$("#searchResultListUl").html('<div class="searchNoResult"><img src="/static/userCenter/img/nosearch.png"><p>没有相关问题，换个关键词试试</p></div>');
			$("#searchResultList").show();
			return;
		}
		$.each(data.data,function(k,v){
			var thisTitle = v.problemName;
			thisTitle = thisTitle.replace(/<span style=\'color:red\'>/g,"");
			thisTitle = thisTitle.replace(/<\/span>/g,"");
			html += '<li>'
					  +'<a href="#" class="item-link item-content openQA" title="'+thisTitle+'" qa="'+v.problemAnswer+'">'
						+'<div class="item-media"><i class="icon-search"></i></div>'
						+'<div class="item-inner">'
						  +'<div class="item-title">'+v.problemName+'</div>'
						  +'<div class="item-after"></div>'
						+'</div>'
					  +'</a>'
					+'</li>';
		})
		$("#searchResult_loading").hide();
		$("#searchResultListUl").html(html);
		$("#searchResultList").show();
	});
}

function checkShowServiceInfo(){

	thisQATitle = '在线客服联系方式';
	thisQAContent = '';
	
	$("#serviceMainPage").addClass('cached');
	$("#qaDetailPage").removeClass('cached');
	
	$("#qaDetailTitle").html(thisQATitle);
	$("#qaDetailContent").html(thisQAContent);
	
	$(".useFull").removeClass('useFullOn');
	$(".unUseFull").removeClass('unUseFullOn');
	
	var isUseFull = getUserFul(thisQATitle);
	if(isUseFull){
		$(".useFull").addClass('useFullOn');
		$(".unUseFull").removeClass('unUseFullOn');
	}
	
	var isUnUseFull = getUnUserFul(thisQATitle);
	if(isUnUseFull){
		$(".useFull").removeClass('useFullOn');
		$(".unUseFull").addClass('unUseFullOn');
	}

	$.ajax({
		type: 'POST',
		url: '/bbs/customerService',
		dataType: 'json',
		data:{format:'json',productId:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success: function(data){
			if(data.status == true){
				var thisHtml = data.data.serviceInfo;
				thisHtml = thisHtml.replace(/\|/g,"<br>");
				$("#qaDetailContent").html(thisHtml);
				$(".qaListLoading").remove();
			}
		},
		error:function(){
		}
	});
}

function initServicePage(){
	var status = doActionFunction('isSupportService',{});
	if(status != '1'){
		return checkShowServiceInfo();
	}
	
	loadSearchResult('充值',function(data){
		doKeywordShow(data,"payQaList");
	})
	loadSearchResult('支付',function(data){
		doKeywordShow(data,"payQaList");
	})
	
	loadSearchResult('帐号',function(data){
		doKeywordShow(data,"userQaList");
	})
	loadSearchResult('登录',function(data){
		doKeywordShow(data,"userQaList");
	})
	loadSearchResult('角色',function(data){
		doKeywordShow(data,"giftQaList");
	});
	
	setTimeout(function(){
		if($(".qaListLoading").length > 0){
			$(".qaListLoading").remove();
			
		}
	},10000);
}

function doKeywordShow(data,id){
	if(data.data.length > 0){
		var html = '';
		for(var i = 0 ; i < data.data.length;i++){
			var v = data.data[i];
			var isFristUi = '';
			if(i == 0){
				isFristUi = ' fristQaLi ';
			}
			if(v.problemName == undefined){
				continue;
			}
			var thisTitle = v.problemName;
			thisTitle = thisTitle.replace(/<span style=\'color:red\'>/g,"");
			thisTitle = thisTitle.replace(/<\/span>/g,"");
			
			html += '<div class="qaListBox '+isFristUi+' openQA" title="'+thisTitle+'" qa="'+v.problemAnswer+'">'
						+'<div class="qaList-left">'+thisTitle+'</div>'
						+'<div class="qaList-right"></div>'
					+'</div>';
		}
		$("#" + id).html(html);
		$(".qaListLoading").remove();
		$("#" + id).parent().css('display','flex');
		$(".waitShow").show();
	}
}

function loadLocalSearchLogs(){
	var searchLog = localStorage.getItem('searchList');
	if(searchLog == null){
		return;
	}
	try{
		var localList = JSON.parse(searchLog);
	}catch(e){
		var localList = new Array();
	}
	for(i = 0 ; i < localList.length ; i++){
		var searchHtml = '';
		for(var i in localList){
			searchHtml += '<a href="javascript:void(0)" class="hotSearchItem">'+localList[i]+'</a>';
		}
		$("#localSearch").html(searchHtml);
	}
}

$(document).on('pageBeforeAnimation', '.page[data-page="searchQa"]', function (e) {
	/*$("#searchKey").val('');
	$("#searchResult").hide();
	$("#searchTips").show();
	$(".clearSearchKey").hide();
	loadLocalSearchLogs();
	*/
})

$(document).on('pageBeforeAnimation', '.page[data-page="searchQa"]', function (e) {
	loadLocalSearchLogs();
})

function loadSearchResult(keyword,callback){
	$.ajax({
		type: 'POST',
		url: 'http://kfapi.quickapi.net/api/searchQuestion?callback=jsonCallback',
		dataType: 'jsonp',
		jsonpCallback:'jsonCallback',
		crossDomain:true,
		data:{format:'json',productId:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode,keyword:keyword},
		success: function(data){
			var result =JSON.parse(data);
			callback(result);
		},
		error:function(){
		}
	});
}

var orderWhere = 1;
function loadOrderList(page){
	$.ajax({
		type: 'GET',
		url: '/app/getOrderList',
		dataType: 'json',
		data:{format:'json',page:page,whereType:orderWhere,gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success: function(data){
			orderIsLoad = false;
			var html = '';
			var list = data.data;
			if(list.length == 0){
				if($(".noOrderList").length <= 0){
					if(page == 1){
						$("#orderListList").html('<div class="centerNoMessage noOrderList"><img src="/static/userCenter/img/nonet.png"><p>无符合条件的记录</p></div>');
					}
				}
				myApp.detachInfiniteScroll($('.orderPull'));
				$('#orderLoadMore').hide();
				myApp.pullToRefreshDone();
				return;
			}
			for(var i = 0 ; i < list.length ; i++){
		
				html += '<li class="item-content">'
							 +'<div class="item-media"><img class="orderIcon lazy" data-src="'+(list[i].appInfo?list[i].appInfo.logo:'/static/app/img/lazy_90_90.png')+'" src="/static/app/img/lazy_90_90.png"></div>'
							 +'<div class="item-inner"  style="height:103px">'
								+'<div class="orderTitle">'
									+'<div class="leftWord">'+list[i].orderSubject+' <span class="payCenterAmount">￥'+list[i].amount+'</span></div>'
									+'<div class="leftWord gray12">'+list[i].timeSay+'<span class="orderStatus red">['+list[i].statusSay+']</span></div>'
									+'<div class="leftWord fz12">'+list[i].orderNo+'</div>'
							  +'</div>'
							 +'</div>'
						+'</li>';
			}
			
			if(page == 1){
				$("#orderListList").html('').html(html);
			}else{
				$("#orderListList").append(html);
			}
			$('img.lazy').trigger('lazy');
			if(list.length == 20){
				$("#orderLoadMore").show();
			}
			if(list.length < 20){
				$("#orderLoadMore").hide();
			}
			$("#orderListList").attr('page',parseInt(page)+1);
			myApp.pullToRefreshDone();			
		},
		error:function(){
			orderIsLoad = false;
			myApp.pullToRefreshDone();
		}
	});
}


function loadThread(page){
	$.ajax({
		type: 'POST',
		url: '/bbs/threadList?page='+page,
		dataType: 'json',
		data:{format:'json',gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success: function(data){
			bbsIsLoad = false;
			myApp.pullToRefreshDone();

			if(data.data.length <= 0){
				if(page == 1){
					if(data.status == true){
						$("#bbsList").html('<div class="centerNoMessage"><img src="/static/userCenter/img/nonet.png"><p>快来做第一个吃螃蟹的人吧</p></div>');
						$(".writeThreadIcon").show();
					}else{
						$("#bbsList").html('<div class="centerNoMessage"><img src="/static/userCenter/img/nonet.png"><p>'+data.error+'</p></div>');
					}
				}else{
					//加载完成
					$("#loadMoreThread").hide();
					myApp.detachInfiniteScroll(".bbsPull");
				}
				return;
			}else{
				if(page == 1){
					$("#bbsList").html('');
				}
				var html = '';
				$(".writeThreadIcon").show();
				$.each(data.data,function(k,v){

					if(v.imgList.length > 0){
						html += 
							'<div class="bbsBox threadLink" tid="'+v.tid+'">'
							+'	<div class="bbsTitle" id="threadTitle'+v.tid+'">'+v.subject+'</div>'
							+'	<div class="bbsContent">'+v.summary+'</div>'
							+'	<div class="bbsImage"><img class="lazy" src="/static/userCenter/img/noGameShot.png" data-src="'+v.imgList[0]+'"></div>'
							+'	<div class="bbsDate">'
							+'		<span class="bbsShowArea"><i class="bbsIcon bbsPost"></i><span class="showNums">'+v.posts+'</span></span>'
							+'		<span class="bbsShowArea"><i class="bbsIcon bbsLike"></i><span class="showNums">'+v.recommendAdd+'</span></span>'
							+'	</div>'
							+'</div>'
					}else{
						html += 
							'<div class="bbsBox threadLink" tid="'+v.tid+'">'
							+'	<div class="bbsTitle" id="threadTitle'+v.tid+'">'+v.subject+'</div>'
							+'	<div class="bbsDate">'
							+'		<span class="bbsShowArea"><i class="bbsIcon bbsPost"></i><span class="showNums">'+v.posts+'</span></span>'
							+'		<span class="bbsShowArea"><i class="bbsIcon bbsLike"></i><span class="showNums">'+v.recommendAdd+'</span></span>'
							+'		<span class="bbsShowArea"><i class="bbsIcon bbsActiveTime"></i><span class="showNums">'+v.createTime+'</span></span>'
							+'	</div>'
							+'</div>'
					}

				})
				$("#bbsList").append(html).attr('page',parseInt(page)+1);
				$('img.lazy').trigger('lazy');
				if(data.data.length == 20){
					$("#loadMoreThread").show();
				}
			}
		},
		error:function(){
			bbsIsLoad = false;
			myApp.pullToRefreshDone();
		}
	})
}

//加载新闻详情
function loadNewsDetail(type,id){
	$.ajax({
		type: 'POST',
		url: '/userCenter/getMessageDetail?type='+type+'&id='+id,
		dataType: 'json',
		data:{format:'json',gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success: function(data){
			myApp.hideIndicator();
			$("#showNewsDetail").html(data.data.content);
			if(data.data.title.length > 0){
				$("#newsTitle").html(data.data.title).show();
			}else{
				$("#newsTitle").hide();
			}
		},
		error:function(){
			myApp.hideIndicator();
		}
	})
}

//加载最近的公告
function loadNoticePage(nowPage){
	$.ajax({
		type: 'POST',
		url: '/userCenter/getMessageCenter?page='+nowPage,
		dataType: 'json',
		data:{format:'json',gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success: function(data){	
			noticeIsLoad = false;
			myApp.pullToRefreshDone();
			if(data.data.length <= 0){
				if(nowPage == 1){
					$("#noticeList").html('<div class="centerNoMessage"><img src="/static/userCenter/img/nonotice.png"><p>暂无更多活动啦</p></div>');
				}
				return;
			}else{
				if(nowPage == 1){
					$("#noticeList").html('');
				}
				var html = '';
				$.each(data.data,function(k,v){
					html += '<div class="noticeBox openNews" title="'+v.showName+'" type="'+v.type+'" aid="'+v.id+'">'
								+'<div class="noticeContent">'
									+'<p>'+(v.isRead=='1'?'':'<i class="msg-point"></i>')+v.showName+'</p>'
									+'<div>'+v.showDesc+'</div>'
								+'</div>'
								+'<div class="seeMoreTips"></div>'
							+'</div>';
				})

				$("#noticeList").append(html);
			}
		},
		error:function(e,e1,e2){
			noticeIsLoad = false;
			myApp.pullToRefreshDone();
		}
	})
}

$(document).on('click','.callSDK',function(){
	var action = $(this).attr('action');
	var params = '';
	doActionFunction(action,params);
})

function doActionFunction(action,params){
	var sendObject = new Object;
	sendObject.action = action;
	sendObject.params = params;
	
	var ua = navigator.userAgent;
	console.log('CallPlatformFunction:' + JSON.stringify(sendObject));
	//为hermesgameAndroid
	if(ua.indexOf('hermesgameAndroid') != -1 || ua.indexOf("hermesgameiOS") != -1){
		if(typeof(JObject) == 'object'){
			return JObject.callAndroidFunction(JSON.stringify(sendObject));
		}
	}
 
 
 	return false;
}

//upload file
$(document).on('click','.selectImageBack',function(){
	var type = $(this).attr('type');
	var params = {type:type,rate:50,maxSelectNum:9};
	doActionFunction('selectImage',params);
	myApp.showIndicator();
})

function onNativeCallback(params){
	var params = base64_decode(params);
	params = JSON.parse(params);
	switch(params.action){
		case 'selectImage':
			uploadImages(params.params);
			break;
		case 'onBackButtonClick':
			//如果有iframe注册返回事件  则由子页面控制返回事件
			if(hasRegisterBackButton){
				var iFrame = document.getElementById('siteFrame');
				if(iFrame){
					var postMessage = new Object();
					postMessage.action = "onBackButtonClick";
					postMessage.params = null;
					iFrame.contentWindow.postMessage(postMessage, '*');
				}else{
					hasRegisterBackButton = false;
				}
			}else{
				if(hasOpenWebSite){
					myApp.hideIndicator();
					$(".preloader-indicator-modal").css('margin-left','0px');
					checkResetScreenType();
					hasOpenWebSite = 0;
					websiteClose();
					return;
				}
				backButtonClick();
			}
			break;
		case 'reloadUserCenterHome':
			$("#realNameNow").hide();
			$("#unRealNameNow").show();
			break;
		case 'userCenterClosed':
		
			break;
			
		case 'completePayPassword':
			var iFrame = document.getElementById('siteFrame');
			if(iFrame){
				var postMessage = new Object();
				postMessage.action = "completePayPassword";
				postMessage.params = params.params;
				iFrame.contentWindow.postMessage(postMessage, '*');
			}
			break;
	}
}

function backButtonClick(){
	doActionFunction('closeUserCenter',{});
}

window.onerror = function(s1,s2,s3){
	
}

function getReferer(){
	var domain=window.location.host;
	return 'http://'+domain+'/gameCenter/wxReferer?productCode=41570977473454629776402125313017';
}

//上传头像
function uploadAvatar(params){
	
	var thisImageList = params.path;
	if(thisImageList.length <= 0){
		return myApp.hideIndicator();
	}
	var thisAvatar = params.path[0];
	var params = {baseCode:thisAvatar,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode};
	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: '/bbs/avatar',
		data: params,
		success: function(response){
			myApp.hideIndicator();
			if(true == response.status){
				var imageHtm = '<img class="userAvatar" id="changeImages" src="data:image/png;base64,' + thisAvatar + '">';
				$("#selectAvatar").html(imageHtm);
			}else{
				return layer.open({content: '更新头像失败',skin: 'msg',time: 3,anim:false });
			}
		},
		timeout:30000,
		error: function(s){
			myApp.hideIndicator();
			return layer.open({content: '网络超时',skin: 'msg',time: 3,anim:false });
		}
	})
}

//处理图片上传
function uploadImages(params){
	if(params.type == 'writeThread'){
		var doc = 'showThreadImagePre';
	}else if(params.type == 'writePost'){
		var doc = 'showPostImagePre';
	}else if(params.type == 'avatar'){
		return uploadAvatar(params);
	}else if(params.type == "sellAccount"){
		var iFrame = document.getElementById('siteFrame');
		if(iFrame){
			var postMessage = new Object();
			postMessage.action = "selectImage";
			postMessage.params = params;
			iFrame.contentWindow.postMessage(postMessage, '*');
		}
		return;
	}else{
		return;
	}
	
	var $imgDiv = $('#' + doc);
	var thisImageList = params.path;
	var hasMore = false
	for(var i = 0 ; i < thisImageList.length ; i++){
		if($(".img-items").length >= 10){
			hasMore = true;
			break;
		}else{
			hasMore = false;
		}
		var thisImage = thisImageList[i];
		var html = '<li class="img-items"><img class="thisAddImage" src="data:image/png;base64,' + thisImage + '"></li>';
		imgArray.push(thisImageList[i]);
		$imgDiv.prepend(html);
	}
	myApp.hideIndicator();
	if(hasMore){
		layer.open({content:'最多10张图片',style:(screenType == 1 ? "margin-left:60px" : ""),skin: 'msg',time: 3,anim:false });
	}
}

$(document).on('click','.hasGetCard',function(){
	var code = $(this).attr('code');
	var params = {content:code};
	doActionFunction('copyText',params);
	layer.open({content:'礼包码已复制到剪贴板',style:(screenType == 1 ? "margin-left:60px" : ""),skin: 'msg',time: 3,anim:false });
})

$(document).on('click','.clickLike',function(){
	var checkHasLik = $(this).attr('hasLike');
	if(checkHasLik == '1'){
		return layer.open({content:'已经赞过啦',style:(screenType == 1 ? "margin-left:60px" : ""),skin: 'msg',time: 2,anim:false });
	}
	var params = {id:tid,type:1,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode};
	$.ajax({
		type: 'POST',
		dataType: 'json',
		url: '/bbs/zan',
		data: params,
		success: function(response){
			if(!response.status){
				return layer.open({content:response.error,style:(screenType == 1 ? "margin-left:60px" : ""),skin: 'msg',time: 2,anim:false });
			}
			if(response.data.act == 'up'){
				var thisNum = parseInt($("#threadInfoZan").html())+1;
				$("#threadInfoZan").html(thisNum);
				$("#likeStatus").attr('hasLike','1');
			}else if(response.data.act == 'sub'){
				var nowNum = parseInt($("#threadInfoZan").html())-1;
				nowNum = nowNum <= 0 ? 0 : nowNum;
				$("#threadInfoZan").html(nowNum);
				$("#likeStatus").attr('hasLike','0');
			}
			return layer.open({content:response.data.msg,style:(screenType == 1 ? "margin-left:60px" : ""),skin: 'msg',time: 2,anim:false });
		},
		error: function(){
			// 
		}
	})
})

var heartBeat = setInterval(function(){
					
	sendOnlineHeartbeat(60);

		
},60000)


var heartbeatNum = 1;
function sendOnlineHeartbeat(timeCheck){
	
	$.ajax({
		type: 'GET',
		url: '/userCenter/onlineHeartbeat',
		dataType: 'json',
		data:{format:'json',timeCheck:timeCheck,gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success:function(data){
			if(data.status != true){
				clearInterval(heartBeat);
			}
		},
		error:function(){
			clearInterval(heartBeat);
		},
		timeout:3000
	})
	
	//像服务器投递数据
	if(productInfo.qkProductCode != '' && productInfo.qkProductCode != undefined){
		
		if(heartbeatNum <= 5){
			var eventName = '在线:'+heartbeatNum+'分钟';
		}else if(heartbeatNum  > 5 && heartbeatNum <= 10){
			var eventName = '在线:5-10分钟';
		}else if(heartbeatNum  > 10 && heartbeatNum <= 20){
			var eventName = '在线:10-20分钟';
		}else if(heartbeatNum  > 20 && heartbeatNum <= 30){
			var eventName = '在线:20-30分钟';
		}else if(heartbeatNum  > 30 && heartbeatNum <= 60){
			var eventName = '在线:30-60分钟';
		}else{
			var eventName = '在线:超过60分钟';
		}
		
		putEventData(eventName);
		
		
		heartbeatNum++;
	}
	
}

function putEventData(eventName){
	//像服务器投递数据
	if(productInfo.qkProductCode != '' && productInfo.qkProductCode != undefined){

		$.ajax({
			type: 'GET',
			url: 'https://sdkreport.quickapi.net/msg/p',
			dataType: 'json',
			data:{productCode:productInfo.qkProductCode,eventCode:eventName,deviceId:userInfo.deviceId,channelCode:134},
			success:function(data){
				
			},
			error:function(){
				
			},
			timeout:3000
		})
	}
}

$(document).on('click','.getCard',function(){
	var cateId = $(this).attr('cateId');
	myApp.showIndicator();

	$.ajax({
		type: 'GET',
		url: '/app/getGift?giftId='+cateId,
		dataType: 'json',
		data:{format:'json',gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success: function(data){
			myApp.hideIndicator();
			if(data.data.code != undefined && data.data.code.length > 0){
				//copy
				var params = {content:data.data.code};
				doActionFunction('copyText',params);
				
				$("#giftDesc" + cateId).html('礼包码:' + data.data.code);
				$("#giftButton" + cateId).addClass('hasGetCard').removeClass('getCard').attr('code',data.data.code);
				$("#giftNum" + cateId).html(`已领取该礼包 <a href="javascript:void(0);" id="giftBtn${cateId}" class="giftUse giftCopy" code="${data.data.code}">复制</a>`);
				
				if(data.data.hasCoinsGift == true){
					$('.getCardSuccess').removeClass('floatDown');
					setTimeout(function(){
							$('.getCardSuccess').css('opacity',1).css('transform','translate(0,0)').show().addClass('floatDown');
					},500);
					setTimeout(function(){
						layer.open({content:'恭喜您获得现金奖励，快去个人中心查看余额吧',style:(screenType == 1 ? "margin-left:60px" : ""),skin: 'msg',time: 3,anim:false });
						$('.getCardSuccess').hide();
					},1500);
				}else{
					
					layer.open({content:'礼包码已复制到剪贴板',style:(screenType == 1 ? "margin-left:60px" : ""),skin: 'msg',time: 3,anim:false });
				}
				
				
				
				//改变余额
				$("#userAmount").html(data.data.amount);
				
				
			}else{
				layer.open({content:data.message,style:(screenType == 1 ? "margin-left:60px" : ""),skin: 'msg',time: 3,anim:false });
			}
		},
		error:function(){
			
		}
	});
})


function referAmount(){
    if (userInfo == null) {
        return;
    }
    //$(".showUserbalance").html('<i class="blanceLoad preloader"></i>');
    myApp.showIndicator();
    $.ajax({
        type: 'GET',
        url: '/app/referAmount',
        dataType: 'json',
        data: {
            format: 'json',
            //platform: platform,
            authToken: userInfo.authToken,
            deviceId: userInfo.deviceId,
            channelCode: userInfo.channelCode,
            productCode: productInfo.productCode
        },
        success: function(data) {
        	myApp.hideIndicator();
            $("#userAmount").html(data.data);
        },
        error: function() {myApp.hideIndicator();},
        timeout: 15000
    })
}


$(document).on('click','.QQJoinGroup',function(){
	var type = $(this).attr('type');
	if(type == 'qq'){
		var groupId = $(this).attr('groupId');
		var action = 'QQ_joinGroup';
		var params = {key:groupId};
		doActionFunction(action,params);
	}else{
		var appId = $(this).attr('appId');
		var rawId = $(this).attr('rawId');
		var action = 'wechatFollow';
		var params = {appId:appId,rawId:rawId};
		doActionFunction(action,params);
	}
	
})


//加载礼包
function loadGiftPage(nowPage){
	
	$.ajax({
		type: 'GET',
		url: '/app/getNewsPageList?page='+nowPage+'&type=2',
		dataType: 'json',
		data:{format:'json',gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
		success: function(data){
			myApp.pullToRefreshDone();
			giftIsLoad = false;
			var html = '';
			if(data.data.length <= 0){
				if(nowPage == 1){
					$("#giftList").html('<div class="centerNoMessage"><img src="/static/userCenter/img/nogfit2x.png"><p>更多礼包正在赶来的路上</p></div>');
				}
				return;
			}else{
				if(nowPage == 1){
					$("#giftList").html('');
				}
				$.each(data.data,function(k,v){
					var getStatus = 'getCard';
					var codeContent = '';
					if(v.nowStatus == 1){
						getStatus = 'hasGetCard';
						codeContent = v.code;
					}
					
					var gold = '';
					var openGame = '';
					if(parseInt(v.balanceCoin) > 0 ){
						gold = '<p class="giftGetCon" style="margin-bottom:0;"><i class="glodIocn"></i>账号余额+'+parseInt(v.balanceCoin)+'元</p>';
					}
					if(v.conditionPid > 0){
						openGame = '<p class="giftGetCon openGameCenter" productId="'+v.conditionPid+'" style="margin-bottom:0;text-decoration: underline; line-height:30px;height:30px;margin-top:5px"><i class="glodIocn downandplay"></i>立即下载并试玩</p>';
					}
					
					var imgPath = '';
					if(v.iconPath.indexOf('http') != -1 && v.iconPath.indexOf('site_single') == -1){
						imgPath = ' style="background-image:url(\''+v.iconPath+'\');background-size:44px 44px; border-radius:22px;background-position:0 0" ';
					}
					
					html += '<div class="giftbox" giftId="'+v.id+'">'
								+'<div class="giftIcon" '+imgPath+'></div>'
								+'<div class="giftDesc"><p class="giftDesc_p">'+v.name+'</p><span id="giftDesc'+v.id+'">'+v.desc+'</span><p class="giftGetCon">领取条件:'+v.conditionSay+'</p>'+gold+openGame+'</div>'
								+'<div class="giftAction"><a id="giftButton'+v.id+'" code="'+codeContent+'" cateId="'+v.id+'" href="javascript:void(0)" class="'+getStatus+'"></a></div>'
							+'</div>';
				})
				$("#giftList").append(html);
			}
			
		},
		error:function(){
			myApp.pullToRefreshDone();
			giftIsLoad = false;
		}
	})
}

var autoOpenGame = 0;
var setSrcTimer = null;
var hasOpenWebSite = 0;
var checkSiteOpen;
$(document).on('click','.openGameCenter',function(){ 
	var productId = $(this).attr('productId');
	
	if(hasOpenWebSite == 1){
		clearInterval(checkSiteOpen);
		checkSiteOpen = null;
		myApp.hideIndicator();
		$(".preloader-indicator-modal").css('margin-left','0px');
		checkResetScreenType();
		hasOpenWebSite = 0;
		websiteClose();
		return;
	}
	
	if($("#openH5site").length > 0){
		autoOpenGame = productId;
		var jumpUrl = '/userCenter/gameDetail?productId=' + productId + '&isFromUserCenter=1&screenType=' + screenType;

		// 自定链接，在iframe中加载
		if(jumpUrl != null && jumpUrl != void 0 && jumpUrl != ''){
			// 拼接userInfo
			jumpUrl = `${jumpUrl}&authToken=${userInfo.authToken}&deviceId=${userInfo.deviceId}&productCode=${productInfo.productCode}&channelCode=${userInfo.channelCode}&fromSite=usercenter`

			// window.location.href = jumpUrl
			//doBackMainViews()
			$("#siteFrameLayer").css('left','0px');
			myApp.showIndicator();
			qgamePopupFrame("siteFrameLayer",0);
			hasOpenWebSite = 1;
			if(setSrcTimer != null){
				clearInterval(setSrcTimer);
				setSrcTimer = null;
			}
			setSrcTimer = setTimeout(function(){
				$('#siteFrame').attr('src', jumpUrl);
			},400);

			var iframe = document.getElementById('siteFrame');
			if(iframe){
				iframe.onload = function(){
					parent.myApp.hideIndicator();
					parent.checkResetScreenType();
				};
			}else{
				websiteClose();
				checkResetScreenType();
				layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
			}
			/*if(checkSiteOpen != null){
				clearInterval(checkSiteOpen);
			}
			checkSiteOpen = setTimeout(function(){
				//30秒后
				if($(".preloader-indicator-modal").length > 0){
					clearInterval(checkSiteOpen);
					checkSiteOpen = null;
					myApp.hideIndicator();
					$(".preloader-indicator-modal").css('margin-left','0px');
					websiteClose();
					checkResetScreenType();
					layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
				}
			},15000);*/
			$(".preloader-indicator-modal").css('margin-left','-25px');
			return true
		}
	}
})
$(document).on("click",".deleteAccount",function(){
	$(".dialog-box.delAccount-dialog .dialog-cont-p").html(`是否删除您的账号，删除后此账号的游戏进度将彻底销毁并无法恢复！`)
	$(".dialog-box.delAccount-dialog .dialog-opt-link.yesdel").html("我要删除");
	$(".dialog-box.delAccount-dialog").show();
});
$(document).on("click",".dialog-opt-link",function(){
	var todom=$(this).data("todom");
	if(todom==undefined || todom==''){
		return false;
	}
	$(".dialog-box."+todom).hide();
	if($(this).hasClass("yesdel")){
		myApp.showIndicator();
		$.ajax({
			type: 'POST',
			url: '/userCenter/resetUser',
			dataType: 'json',
			data:{gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
			success: function(res){
				if(res.status==true){
					layer.open({content: res.message,skin: 'msg',time: 2,end:function(){
						doActionFunction("showSwitchAccount",{});
					}});
				}else{
					layer.open({content: res.message,skin: 'msg',time: 3});
				}
			},
			complete:function(){
				myApp.hideIndicator();
			},error:function(){
				layer.open({content: '网络请求错误',skin: 'msg',time: 3});
			}
		});
	}
});

/**
 * 小号
 */
!function(){
	var usrIsLoad = false;

	$('#userList').on('opened', function () {
		loadUserList(1);
	});

	$('#userList').on('open', function () {
		$("#userShowList").html('<div class="centerLoading"><div class="preloader"></div></div>');
	});

	$('.userPull').on('refresh', function (e) {
		if(usrIsLoad) return;
		usrIsLoad = true;
		myApp.attachInfiniteScroll($('.userPull'));
		$("#userListList").attr('page','1');
		loadUserList(1);
	});

	function loadUserList(page){
		$.ajax({
			type: 'GET',
			url: '/app/getAccountList',
			dataType: 'json',
			data:{format:'json',page:page,gameid:productInfo.id,authToken:userInfo.authToken,deviceId:userInfo.deviceId,channelCode:userInfo.channelCode,productCode:productInfo.productCode},
			success: function(data){
				var usrArray = [], list = data.data;

				if(list.length == 0){
					if($(".noUserList").length <= 0){
						if(page == 1){
							$("#userListList").html('<div class="centerNoMessage noUserList"><img src="/static/userCenter/img/nonet.png"><p>无符合条件的记录</p></div>');
						}
					}
					myApp.detachInfiniteScroll($('.userPull'));
					$('#userLoadMore').hide();
					return;
				}
				for(var u of list){
					var avatar = '/static/userCenter/img/photo@3x.png';
					try{
						if(u.hasOwnProperty('avatarUrl') && u.avatarUrl!=""){
							avatar = u.avatarUrl;
						}
					}catch(e){}
					
					
					usrArray.push(`
						<li class="item-content">
							 <div class="item-media"><img class="orderIcon accountIcon" src="${avatar}"></div>
							 <div class="item-inner"  style="height:103px">
							 	<i class="usr-ubind" data-uid="${u.uid}"></i>
								<div class="orderTitle">
									<div class="leftWord">${u.username} <!--<span class="payCenterAmount">${u.nick}</span>--></div>
									<div class="leftWord fz12">UID: ${u.uid}</div>
									<div class="leftWord gray12">注册: ${u.regTime} <!--<span class="orderStatus red">UID:${u.uid}</span>--></div>
							  </div>
							 </div>
						</li>
					`);
				}

				if(page == 1){
					$("#userListList").html('').html(usrArray.join(''));
				}else{
					$("#userListList").append(usrArray.join(''));
				}
				// $('img.lazy').trigger('lazy');
				if(list.length == 20){
					$("#userLoadMore").show();
				}
				if(list.length < 20){
					$("#userLoadMore").hide();
				}
				$("#userListList").attr('page',parseInt(page)+1);
			},
			complete:function(){
				usrIsLoad = false;
				myApp.pullToRefreshDone();
			},error:function(){
				myApp.detachInfiniteScroll($('.userPull'));
			}
		});
	}

	var bindModal, bindStatus = false;
	$('.bindUserBtn').on('click', function(){
		// $(".popup").css('left','60px');
		// myApp.popup("#userBind");

		bindModal = myApp.modal({
			title:  '绑定小号',
			text: '输入账号密码验证并绑定',
			afterText: `
				<div class="input-field modal-input-double"><input type="text" id="modal-username" placeholder="用户名" class="modal-text-input"></div>
				<div class="input-field modal-input-double"><input type="password" id="modal-password" placeholder="密码" class="modal-text-input"></div>
			`,
			buttons: [{
				text: '取消',
			},{
				text: '绑定',
				bold: true,
				close: false,
				onClick: function() {
					if(bindStatus){
						return false;
					}
					bindStatus = true;

					var data = {
						format:'json',
						gameid: productInfo.id,
						authToken: userInfo.authToken,
						deviceId: userInfo.deviceId,
						productCode: productInfo.productCode,
						channelCode: userInfo.channelCode,
						username: $('#modal-username').val().trim(),
						passwd: $('#modal-password').val().trim(),
					}

					if(data.username.length < 1){
						bindStatus = false;
						return layer.open({content: '请输入用户名',skin: 'msg',time: 2,anim:false });
					}
					if(data.passwd.length < 1){
						bindStatus = false;
						return layer.open({content: '请输入密码',skin: 'msg',time: 2,anim:false });
					}

					myApp.showIndicator();
					// $("#submit-bind").addClass('disabled');
					$.ajax({
						type: 'POST',
						dataType: 'json',
						url: `/app/userBind?action=bind&format=json&gameid=${productInfo.id}&authToken=${userInfo.authToken}&deviceId=${userInfo.deviceId}&channelCode=${userInfo.channelCode}&productCode=${productInfo.productCode}`,
						data: data,
						success: function(response){

							if(response.status == true){

								myApp.closeModal(bindModal);
								layer.open({content: '账号绑定成功！',skin: 'msg',time: 2,anim:false });
								setTimeout(function(){
									// $("#bind-user").val('');
									// $("#bind-passwd").val('');
									// myApp.closeModal('#userBind');
									myApp.pullToRefreshTrigger('.userPull');
								}, 2000)
							}else{
								return layer.open({content: response.message,skin: 'msg',time: 3,anim:false });
							}
						},
						timeout:900000,
						error: function(){
							return layer.open({content: '绑定失败网络超时',skin: 'msg',time: 3,anim:false });
						},
						complete: function(){
							bindStatus = false;
							myApp.hideIndicator();
							// $("#submit-bind").removeClass('disabled');
						}
					});
				}
			}]
		});
	});

	/*$(document).on('click', '#submit-bind', function(){
		// 
	});*/

	var ubindModal, ubindUid;
	$(document).on('click', '.usr-ubind', function(){
		ubindUid = $(this).data('uid');

		ubindModal = myApp.modal({
			title:  '解绑小号',
			text: '是否解除此账号的小号关系。',
			buttons: [{
				text: '取消',
			},{
				text: '<span style="color:red;">解绑</span>',
				bold: true,
				close: false,
				onClick: function() {
					var data = {
						format:'json',
						gameid: productInfo.id,
						authToken: userInfo.authToken,
						deviceId: userInfo.deviceId,
						productCode: productInfo.productCode,
						channelCode: userInfo.channelCode,
						uid: ubindUid
					}

					myApp.showIndicator();
					$("#submit-bind").addClass('disabled');
					$.ajax({
						type: 'GET',
						dataType: 'json',
						url: `/app/userBind?action=unbind`,
						data: data,
						success: function(response){

							if(response.status == true){

								myApp.closeModal(ubindModal);
								layer.open({content: '账号解绑成功！',skin: 'msg',time: 2,anim:false });
								setTimeout(function(){
									myApp.pullToRefreshTrigger('.userPull');
								}, 2000)
							}else{
								return layer.open({content: response.message,skin: 'msg',time: 3,anim:false });
							}
						},
						timeout:900000,
						error: function(){
							return layer.open({content: '解绑失败网络超时',skin: 'msg',time: 3,anim:false });
						},
						complete: function(){
							myApp.hideIndicator();
							$("#submit-bind").removeClass('disabled');
						}
					});
				}
			}]
		});
	});
}();
//关闭iframe
$(document).on("click","#siteFrameLayer .close-siteframe-link",function(){
	myApp.hideIndicator();
	$(".preloader-indicator-modal").css('margin-left','0px');
	checkResetScreenType();
	hasOpenWebSite = 0;
	websiteClose();
})

function base64_decode(str)
{
    var base64DecodeChars = [
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
            -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1
        ];
    var c1, c2, c3, c4;
    var i, j, len, r, l, out;
 
    len = str.length;
    if (len % 4 != 0) {
        return '';
    }
    if (/[^ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\+\/\=]/.test(str)) {
        return '';
    }
    if (str.charAt(len - 2) == '=') {
        r = 1;
    }
    else if (str.charAt(len - 1) == '=') {
        r = 2;
    }
    else {
        r = 0;
    }
    l = len;
    if (r > 0) {
        l -= 4;
    }
    l = (l >> 2) * 3 + r;
    out = new Array(l);
 
    i = j = 0;
    while (i < len) {
        // c1
        c1 = base64DecodeChars[str.charCodeAt(i++)];
        if (c1 == -1) break;
 
        // c2
        c2 = base64DecodeChars[str.charCodeAt(i++)];
        if (c2 == -1) break;
 
        out[j++] = String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));
 
        // c3
        c3 = base64DecodeChars[str.charCodeAt(i++)];
        if (c3 == -1) break;
 
        out[j++] = String.fromCharCode(((c2 & 0x0f) << 4) | ((c3 & 0x3c) >> 2));
 
        // c4
        c4 = base64DecodeChars[str.charCodeAt(i++)];
        if (c4 == -1) break;
 
        out[j++] = String.fromCharCode(((c3 & 0x03) << 6) | c4);
    }
    return toUTF16(out.join(''));
}

function toUTF16(str)
{
    if ((str.match(/^[\x00-\x7f]*$/) != null) ||
        (str.match(/^[\x00-\xff]*$/) == null)) {
        return str.toString();
    }
    var out, i, j, len, c, c2, c3, c4, s;
 
    out = [];
    len = str.length;
    i = j = 0;
    while (i < len) {
        c = str.charCodeAt(i++);
        switch (c >> 4) {
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
            // 0xxx xxxx
            out[j++] = str.charAt(i - 1);
            break;
            case 12: case 13:
            // 110x xxxx   10xx xxxx
            c2 = str.charCodeAt(i++);
            out[j++] = String.fromCharCode(((c  & 0x1f) << 6) |
                                            (c2 & 0x3f));
            break;
            case 14:
            // 1110 xxxx  10xx xxxx  10xx xxxx
            c2 = str.charCodeAt(i++);
            c3 = str.charCodeAt(i++);
            out[j++] = String.fromCharCode(((c  & 0x0f) << 12) |
                                           ((c2 & 0x3f) <<  6) |
                                            (c3 & 0x3f));
            break;
            case 15:
            switch (c & 0xf) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                // 1111 0xxx  10xx xxxx  10xx xxxx  10xx xxxx
                c2 = str.charCodeAt(i++);
                c3 = str.charCodeAt(i++);
                c4 = str.charCodeAt(i++);
                s = ((c  & 0x07) << 18) |
                    ((c2 & 0x3f) << 12) |
                    ((c3 & 0x3f) <<  6) |
                     (c4 & 0x3f) - 0x10000;
                if (0 <= s && s <= 0xfffff) {
                    out[j++] = String.fromCharCode(((s >>> 10) & 0x03ff) | 0xd800,
                                                  (s         & 0x03ff) | 0xdc00);
                }
                else {
                    out[j++] = '?';
                }
                break;
                case 8: case 9: case 10: case 11:
                // 1111 10xx  10xx xxxx  10xx xxxx  10xx xxxx  10xx xxxx
                i+=4;
                out[j++] = '?';
                break;
                case 12: case 13:
                // 1111 110x  10xx xxxx  10xx xxxx  10xx xxxx  10xx xxxx  10xx xxxx
                i+=5;
                out[j++] = '?';
                break;
            }
        }
    }
    return out.join('');
}