var myApp = new Framework7({tapHold:true,});
var $ = Dom7;
var mainView = myApp.addView('.view-main', {
    // Enable dynamic Navbar
    dynamicNavbar: true,
    // Enable Dom Cache so we can use all inline pages
    domCache: true,
});


$(document).on("click",".backToMain",function(){
  window.parent.postMessage({"action":"closeFrame"},"*");
})

window.parent.postMessage({"action":"registerBackButton"},"*");


var curBar;
function closeSortModa(){
    $sortModaLayer.css('opacity','0');
    var $dialog = $('.sortBox.sortBoxIn');
    $('.sortBox').removeClass('sortBoxIn');
    setTimeout(function(){
        $sortModaLayer.hide();
    },300)
    // $sortModaLayer.css('top', '100px')
    // $dialog.css('top', '100px')
}
$(document).on('click','.changeView',function(){
    var pageId = $(this).attr('pageId');
    curBar = pageId;
    closeSortModa();
    $(".changeView").removeClass('active');
    $(this).addClass('active');
    $(".goodsList").hide();
    $("#" + pageId).show();
    $('a.sortButton').attr('boxId', 'goodsSortBox');

    if(pageId == 'info'){
        if($('#tradeListBox .my-order-list').length == 0 && $('#tradeListBox .centerNoMessage').length == 0){
            $tradeContainer.attr('nowPage', 0);
            loadTradeList();
        }
    }
    if(pageId == 'my'){
        $('a.sortButton').attr('boxId', 'mySortBox');
        // if($('#orderListBox .my-order-list').length == 0 && $('#orderListBox .centerNoMessage').length == 0){
            // $myContainer.attr('nowPage', 0);
            loadOrderList();
        // }
    }
});

$(document).on('click','.toPage',function(){
	var pageId = $(this).attr('pageId');
	pushHistory('page');
	 mainView.router.load({pageName:pageId});
})

$("#tradeListBox").scroll(function(){
	loadLazyImage("#tradeListBox");
})

$("#goodsList").scroll(function(){
	loadLazyImage(".goodsListBox");
})

/**
 * 商品列表
 */
$(document).on('click',".sortItem.myItem",function(){
    var type = $('.changeMylist.active').attr('pageId'),
        box = 'my-finish';
    if(type == 'sell') box = 'my-sell';
    if(type == 'audit') box = 'my-audit';
    if(type == 'waitPage') box = 'my-pay';

    if($(`#${box} .myListItem`).length == 0){
        closeSortModa();
    }
    var sortVal = $(this).attr('attr')
        ,sortField = 'price'
        ,sortData = [];
    if(sortVal == 3) sortField = 'time';
    
    $(`#${box} .myListItem`).each(function(i, v){
        sortData.push($(v));
    });
    if(sortData.length == 0){
        closeSortModa();
        return;
    }

    sortData.sort(function(item2, item1){
        var val2 = $(item2).data(sortField), val1 = $(item1).data(sortField);
        if(sortVal == 1){
            return val1 - val2;
        }
        if(sortVal == 2){
            return val2 - val1;
        }
        if(sortVal == 3){
            return val1 - val2;
        }
    });

    var dom = []
    $.each(sortData, function(i, v){
        dom.push($(v).prop('outerHTML'));
    });
    $(`#${box}`).html(dom.join(''));
	
	setTimeout(function(){
		loadLazyImage(".orderListBox");
	},50);
	
    closeSortModa();
});

var reloadGoods = false; //返回列表页是否刷新
var $goodsContainer = $('#goodsList')
    ,$myContainer = $('#my')
    ,$tradeContainer = $('#info')
    ,goodsPageLoad = false
    ,myInfoPageLoad = false
    ,tradePageLoad = false;
var reloadFinish = true,
    reloadSell = true,
    reloadAudit = true,
    reloadPay = true;
function loadOrderList(){

    var nowPage = 0,
        type = $('.changeMylist.active').attr('pageId'),
        divBox = '#my-finish';

    if(type == 'payed'){
        divBox = '#my-finish';
        if(reloadFinish == false){
            $('#orderListBox > div').hide();
            $(divBox).show();
            return false;
        }
        reloadFinish = false;
    }
    if(type == 'waitPage'){
        divBox = '#my-pay';
        if(reloadPay == false){
            $('#orderListBox > div').hide();
            $(divBox).show();
            return false;
        }
        reloadPay = false;
    }
    if(type == 'sell'){
        divBox = '#my-sell';
        if(reloadSell == false){
            $('#orderListBox > div').hide();
            $(divBox).show();
            return false;
        }
        reloadSell = false;
    }
    if(type == 'audit'){
        divBox = '#my-audit';
        if(reloadAudit == false){
            $('#orderListBox > div').hide();
            $(divBox).show();
            return false;
        }
        reloadAudit = false;
    }

    var pageId = $('.changeMylist.active').attr('pageId')
    if(pageId == 'sell' || pageId == 'audit'){
        loadMyGoods();
        return false;
    }

	

    nowPage = $(divBox).attr('nowPage');
    nowPage++;
    $.ajax({
        type: 'GET',
        url: '/userCenter/getMyOrderList?page=' + nowPage + '&sort=' + sortType + '&q=' + kw,
        dataType: 'json',
        data: {
            format: 'json',
            "companyId": config.manageUid,
            platform: platform,
            authToken: authToken,
            deviceId: config.deviceId,
            productCode: config.productCode,
            type: type
        },
        success: function(data) {
            try {
                var c = data.data;
            } catch (e) {
                return;
            }

            $(divBox).attr('nowPage', nowPage);

            if(c.rows > 0){
                if (nowPage == 1) {
                    $(divBox).html(c.cnt);
                } else {
                    $(divBox).append(c.cnt);
                }
            }else{

                if (nowPage == 1) {
                    $(divBox).html(c.cnt);
                }else{
                    if($(divBox).find('.dropload-noData').length == 0){
                        $(divBox).append('<div class="dropload-noData"><em class="no-data"></em>暂无更多数据</div>');
                    }
                }
                myApp.detachInfiniteScroll($myContainer);
            }
            $('#orderListBox > div').hide();
            $(divBox).show();
			
			setTimeout(function(){
				loadLazyImage(".orderListBox");
			},50);
        },
        complete: function(xhr, type) {
            myInfoPageLoad = false;
            $myContainer.find('.infinite-scroll-preloader').hide();
            // $myContainer.find('.loadBox').hide();
        }
    });
}

$(document).on('pageBeforeAnimation', '.page', function (e) {
	var page = e.detail.page.url;
	
	if(page == '#index' || page == '#sellPage' || page == '#gameList' || page == '#userList' || page == '#qaDetail'){
		doActionFunction('setActionBarColor',{color:'#f7f7f8'});
	}
	
	if(page == '#goodsDetail' || page == '#payNowPage' ){
		doActionFunction('setActionBarColor',{color:'#ffffff'});
	}
	
})

$(document).on('click','.fastSearchChannel',function(){
	var key = $(this).find('div').html();
	var hasSet = 0;
	$.each($(".gameItem"),function(k,v){
		if($(v).attr('code') == key && hasSet == 0){
			var toTop = $(v).offset().top - 50 - config.statusBarHeight;
			if(toTop > 0){
				var nowScorll = $("#selectScrollBody").scrollTop();
				for(var i = 0 ; i <= toTop;i++){
					$("#selectScrollBody").scrollTo(0,nowScorll+i,200);
				}
			}else{
				var nowScorll = $("#selectScrollBody").scrollTop();
				for(var i = 0 ; i >= toTop;i--){
					$("#selectScrollBody").scrollTo(0,nowScorll+i,200);
				}
			}
			
			hasSet = 1;
			return false;
		}
	});
})

function loadGoodsList(){
    $goodsContainer.find('.infinite-scroll-preloader').show();

    var nowPage = $goodsContainer.attr('nowPage');
    nowPage++;

    $.ajax({
        type: 'GET',
        url: '/userCenter/getGoodsPageList?page=' + nowPage + '&sort=' + sortType + '&q=' + kw,
        dataType: 'json',
        data: {
            format: 'json',
            companyId: config.manageUid,
            platform: platform,
            authToken: authToken,
            deviceId: config.deviceId,
            productCode: config.productCode,
        },
        success: function(data) {

            goodsPageLoad = false;
            try {
                var list = data.data;
            } catch (e) {
                return;
            }
            if (nowPage == 1) {
                setTimeout(function(){
                    $(".defaultHide").removeClass('defaultHide');
                    window.parent.postMessage({"action":"hideLoading","from":"goodsList"},"*");
                },200);
                if(list.length == 0){
                    goodsPageLoad = true;
					donePullReferer();
                    $(".goodsListBox").html('<div class="centerNoMessage"><img src="/static/userCenter/img/nonotice.png"><p>暂无交易信息</p></div>');
                    return;
                }
				if(list.length < 20){
					myApp.detachInfiniteScroll($goodsContainer);
				}
            }

            $goodsContainer.attr('nowPage', nowPage);
            if (list.length == 0) {
                myApp.detachInfiniteScroll($goodsContainer);
                return;
            }

            var arr = []
            for (var v of list) {
                var attrs = '', img = '';
                if(v.vipLevel > 0) attrs = `VIP${v.vipLevel}`
				if(v.serverName) attrs += `${attrs ? ' / ' : ''}${v.serverName}`
				if(v.images.length > 0){
					img = `<div class="goodsIcon"><div class="goodsImageShow lazy" lazyType="background" id="gameShowPic${v.id}" data-src="${v.images[0]}" style="background-image:url(/static/app/img/pre/2_3.png)"></div></div>`;
				}

				arr.push(`
					<div class="goodsBox openGoodsDetail" goodsId="${v.id}">
					${img}
					<div class="goodsDesc ${img?'':'noImg'}">
					<div class="goodsGame">${v.goodsName}</div>
					<div class="goodsServer">${attrs}</div>
					<div class="rolePay">充值总额：${v.allPayAmount}</div>
					<div class="goodsPrice"><span class="font10">￥</span>${v.price}  <span class="priceTime">${v.timeSay}</span>${v.isreview==1 ? '<!--<i class="hasReview"></i>-->' : ''}</div>
					</div>
					</div>
					`)
			 }
			if (nowPage == 1) {
				$(".goodsListBox").html(arr.join(''));
			} else {
				$(".goodsListBox").append(arr.join(''));
			}
			donePullReferer();
			loadLazyImage(".goodsListBox");
        },
		error:function(){
			donePullReferer();
		},
		complete: function(xhr, type) {
			$goodsContainer.find('.infinite-scroll-preloader').hide();
		}
	});
}

function closeDeleteLayer(){
    $(".deleteLayer").css('visibility','hidden').css('display','none').css('opacity',0);
}
$(document).on('click','.orderActionButtonNom',function(){
	var action = $(this).data('act'), goodsId = $(this).attr('gid');
	$(".deleteLayer").css('visibility','visible').css('display','block').css('opacity',1);

    if(action == 'disableGoods' || action == 'enableGoods' || action == 'dropGoods'){
        var tips = '是否暂停此小号的出售？';
        if(action == 'enableGoods') tips = '是否发布此小号的出售？';
        if(action == 'dropGoods') tips = '是否删除此小号商品？';

    	var GoodsModal = myApp.modal({
    		title:  '提示',
    		text: tips,
    		
    		buttons: [{
    			text: '取消',
    			onClick:function(){
                    myApp.closeModal(GoodsModal);
    				closeDeleteLayer();
                }
    		},{
    			text: '<span style="color:red;">确定</span>',
    			bold: true,
    			close: false,
    			onClick: function() {
    				myApp.showIndicator();
    				$.ajax({
                        type: 'GET',
                        url: '/userCenter/resetGoods',
                        dataType: 'json',
                        data: {
                            format: 'json',
                            "companyId": config.manageUid,
                            platform: platform,
                            authToken: authToken,
                            deviceId: config.deviceId,
                            productCode: config.productCode,
                            goodsId: goodsId,
                            action: action,
                        },
                        success: function(data) {
                            if(data.status != true){
                                layer.open({content: data.message,skin: 'msg',time: 4,anim:false });
                                return false;
                            }

                            if(action == 'dropGoods'){
                                $(`#my-sell-item-${goodsId}`).remove();
                                if($('#my-sell .my-sell-item').length == 0){
                                    $('#my-sell').html('<div class="centerNoMessage"><img src="/static/userCenter/img/nonotice.png"><p>暂无交易信息</p></div>');
                                }
                            }

                            if(action == 'disableGoods'){
                                $(`#my-sell-item-${goodsId} .orderSubmitDate span`).eq(1).hide();
                                $(`#my-sell-item-${goodsId} .orderSubmitDate span`).eq(0).show();
                                $(`#my-sell-item-${goodsId} .orderActionButtonNom`).eq(2).attr('data-act', 'enableGoods').text('上架商品');
                            }

                            if(action == 'enableGoods'){
                                $(`#my-sell-item-${goodsId} .orderSubmitDate span`).eq(0).hide();
                                $(`#my-sell-item-${goodsId} .orderSubmitDate span`).eq(1).show();
                                $(`#my-sell-item-${goodsId} .orderActionButtonNom`).eq(2).attr('data-act', 'disableGoods').text('临时下架');
                            }

                            $goodsContainer.attr('nowPage', 0);
                            // switchToGoods();
                            loadGoodsList();

                            /*setTimeout(function(){
                                switchToMyGoods(true);
                            },500);*/
                        },
                        complete: function() {
                            myApp.hideIndicator();
                        },
                        error: function() {
                            layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
                        }
                    });
    				
    				myApp.closeModal(GoodsModal);
    				closeDeleteLayer();
    			}
    		}]
    	});
    }

    if(action == 'editGoods'){

        var GoodsModal = myApp.modal({
            title:  '提示',
            text: '修改操作将下架该商品，并可能需要重新审核，确认继续吗？',
            
            buttons: [{
                text: '取消',
                onClick:function(){
                    myApp.closeModal(GoodsModal);
                    closeDeleteLayer();
                }
            },{
                text: '<span style="color:red;">确定</span>',
                bold: true,
                close: false,
                onClick: function() {
                    myApp.closeModal(GoodsModal);
                    closeDeleteLayer();

                    resetEditForm();
                    sellAction = 'edt';
					pushHistory('page');
                    mainView.router.load({pageName: 'modifygoods', query:{goodsId:goodsId}});
                }
            }]
        });
    };
});
$(document).on('click','.dropOrder',function(){
    var orderno = $(this).data('orderno');
    $(".deleteLayer").css('visibility','visible').css('display','block').css('opacity',1);

    var GoodsModal = myApp.modal({
        title:  '提示',
        text: '是否取消该订单？',
        
        buttons: [{
            text: '关闭',
            onClick:function(){
                myApp.closeModal(GoodsModal);
                closeDeleteLayer();
            }
        },{
            text: '<span style="color:red;">取消订单</span>',
            bold: true,
            close: false,
            onClick: function() {
                myApp.showIndicator();
                $.ajax({
                    type: 'GET',
                    url: '/userCenter/resetGoods',
                    dataType: 'json',
                    data: {
                        format: 'json',
                        "companyId": config.manageUid,
                        platform: platform,
                        authToken: authToken,
                        deviceId: config.deviceId,
                        productCode: config.productCode,
                        orderNo: orderno,
                        action: 'dropOrder',
                    },
                    success: function(data) {
                        if(data.status != true){
                            layer.open({content: data.message,skin: 'msg',time: 4,anim:false });
                            return false;
                        }

                        $(`#my-order-item-${orderno}`).remove();
                        if($('#my-pay .myListItem').length == 0){
                            $('#my-pay').html('<div class="centerNoMessage"><img src="/static/userCenter/img/nonotice.png"><p>暂订单易信息</p></div>');
                        }

                        $goodsContainer.attr('nowPage', 0);
                        loadGoodsList();
                    },
                    complete: function() {
                        myApp.hideIndicator();
                    },
                    error: function() {
                        layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
                    }
                });
                
                myApp.closeModal(GoodsModal);
                closeDeleteLayer();
            }
        }]
    });

});
function chkEditForm(){
    var f = {
        title: $('#edt-title').val().trim(),
        serverName: $('#edt-server').val().trim(),
        level: parseInt($('#edt-level').val()),
        price: parseFloat($('#edt-price').val()),
        text: $('#edt-text').val().trim()
    },
    stat = (f.title != '') && (f.serverName != '') && (!isNaN(f.level) && f.level > 0) && (!isNaN(f.price) && f.price > 0) && (f.text != '');
    stat = stat && (imgArray.length > 0);
    if(stat){
        $('.submit.edt-account').removeClass('disabled');
    }else{
        $('.submit.edt-account').addClass('disabled');
    }
}
function resetEditForm(){
    imgArray = [];
    $('#modifygoods .runToPageSelect').eq(0).text('');
    $('#modifygoods .runToPageSelect').eq(1).text('');
    $('#edt-id').val(0);
    $('#edt-server').val();
    $('#edt-level').val();
    $('#edt-text').val();
    $('#edt-title').val();
    $('#edt-price').val();
    $("#showAccountImage2 .img-items").remove();
    $('.submit.edt-account').addClass('disabled');
}
$(document).on('keyup', '#edt-title,#edt-server,#edt-level,#edt-price,#edt-text', chkEditForm);
// $(document).on('pageAfterAnimation', '.page[data-page=modifygoods]', function (e) {
$(document).on('pageBeforeAnimation', '.page[data-page=modifygoods]', function (e) {
    var pageData = e.detail.page.query;

    // 再次出售
    if(pageData.hasOwnProperty('action') && pageData.action == 'resell'){
        $('#modifygoods .runToPageSelect').eq(0).text(pageData.game.showName);
        productId = pageData.game.productId;
        $('#modifygoods .runToPageSelect').eq(1).text(pageData.user.username);
        userId = pageData.user.uid;

        $('#edt-action').val('resell');
        $('#edt-order').val(pageData.orderNo);
        $('#edt-server').val(pageData.serverName);
        $('#edt-level').val(pageData.vipLevel);
        $('#edt-text').val(pageData.desc);
        $('#edt-title').val(pageData.goodsName);
        $('#edt-price').val(pageData.price);

        $('#showAccountImage2 li.img-items').remove();
        imgArray = [];
        if(pageData.images.length > 0){
            var imgHtml = '';
            $.each(pageData.images,function(k,v){
                imgHtml += '<li class="img-items"><img num="'+k+'" imageData="'+v+'" class="thisAddImage" src="'+v+'"></li>';
                imgArray.push(v);
            });
            $("#showAccountImage2").prepend(imgHtml);
        }

        chkEditForm();
        return false;
    }

    // 编辑商品
    var goodsId = e.detail.page.query.goodsId;
    myApp.showIndicator();
    $.ajax({
        type: 'GET',
        url: '/userCenter/getGoodDetail?act=md&id=' + goodsId,
        dataType: 'json',
        data: {
            format: 'json',
            companyId: config.manageUid,
            platform: platform,
            authToken: authToken,
            deviceId: config.deviceId,
            productCode: config.productCode,
        },
        success: function(data) {
            var r, imgHtml = '';
            try{
                r = data.data;
            }catch(e){
				myApp.hideIndicator();
                layer.open({content: '获取商品信息失败！',skin: 'msg',time: 4,anim:false });
                mainView.router.back();
                return;
            }
            
            $('#modifygoods .runToPageSelect').eq(0).text(r.gameData.showName);
            $('#modifygoods .runToPageSelect').eq(1).text(r.user.username);
            $('#edt-action').val('edit');
            $('#edt-id').val(goodsId);
            $('#edt-server').val(r.serverName);
            $('#edt-level').val(r.vipLevel);
            $('#edt-text').val(r.desc);
            $('#edt-title').val(r.goodsName);
            $('#edt-price').val(r.price);

            $.each(r.images,function(k,v){
                imgHtml += '<li class="img-items"><img num="'+k+'" imageData="'+v+'" class="thisAddImage" src="'+v+'"></li>';
                imgArray.push(v);
            });
            $("#showAccountImage2").prepend(imgHtml);

            chkEditForm();
        },
        complete: function() {
            myApp.hideIndicator();
        },
        error: function() {
            layer.open({content: '获取商品信息失败！',skin: 'msg',time: 4,anim:false });
            mainView.router.back();
        }
    });

});
$(document).on('click', '.submit.edt-account', function(){

    var $btn = $(this),
    formData = {
        format: 'json',
        companyId: config.manageUid,
        platform: platform,
        authToken: authToken,
        deviceId: config.deviceId,
        productCode: config.productCode,
        goodsId: parseInt($('#edt-id').val()),
        title: $('#edt-title').val().trim(),
        serverName: $('#edt-server').val().trim(),
        level: parseInt($('#edt-level').val()),
        price: parseFloat($('#edt-price').val()),
        text: $('#edt-text').val().trim(),
        imgList: imgArray.join(','),
        action: $('#edt-action').val().trim(),
        productId: productId,
        userId: userId
    }

    if(formData.action == 'edit' && formData.goodsId < 1){
        layer.open({content: '商品选择错误',skin: 'msg',time: 2,anim:false });
        return;
    }
    if(formData.title == ''){
        layer.open({content: '请填写交易名称',skin: 'msg',time: 2,anim:false });
        return;
    }
    if(formData.serverName == ''){
        layer.open({content: '请填写区服',skin: 'msg',time: 2,anim:false });
        return;
    }
    if(isNaN(formData.level) || formData.level < 1){
        layer.open({content: '请如实填写VIP等级',skin: 'msg',time: 2,anim:false });
        return;
    }
    if(isNaN(formData.price) || formData.price <= 0){
        layer.open({content: '请填写售价',skin: 'msg',time: 2,anim:false });
        return;
    }
    if(formData.text == ''){
        layer.open({content: '请填写账号描述',skin: 'msg',time: 2,anim:false });
        return;
    }
    if(imgArray.length == 0){
        layer.open({content: '请上传至少一张截图',skin: 'msg',time: 2,anim:false });
        return;
    }

    $btn.addClass('disabled');
    myApp.showIndicator();
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/userCenter/sellAccount',
        data: formData,
        success: function(response){

            if(true != response.status){
                return layer.open({content: response.message,skin: 'msg',time: 3,anim:false });
            }

            if(formData.action == 'resell'){
                var order = $('#edt-order').val();
                $(`#my-order-item-${order}`).remove();

                resetEditForm();
                reloadSell = true;
                switchToMyGoods(true);
                $goodsContainer.attr('nowPage', 0);
                loadGoodsList();
                return false;
            }

            resetEditForm();
            reloadSell = true;
            switchToMyGoods(true);
            $goodsContainer.attr('nowPage', 0);
            // switchToGoods();
            loadGoodsList();
        },
        timeout:900000,
        complete: function(){
            $btn.removeClass('disabled');
            myApp.hideIndicator();
        },
        error: function(s){
            return layer.open({content: '发布失败网络超时',skin: 'msg',time: 3,anim:false });
        }
    })

})

function loadTradeList(){
    $tradeContainer.find('.infinite-scroll-preloader').show();

    var nowPage = $tradeContainer.attr('nowPage');
    nowPage++;

    $.ajax({
        type: 'GET',
        url: '/userCenter/getGoodsTrade?page=' + nowPage + '&sort=' + sortType + '&q=' + kw,
        dataType: 'json',
        data: {
            format: 'json',
            companyId: config.manageUid,
            platform: platform,
            authToken: authToken,
            deviceId: config.deviceId,
            productCode: config.productCode,
        },
        success: function(data) {
            try {
                var c = data.data;
            } catch (e) {
                return;
            }

            $tradeContainer.attr('nowPage', nowPage);

            if(c.rows > 0){
                if (nowPage == 1) {
                    $("#tradeListBox").html(c.cnt);
                } else {
                    $("#tradeListBox").append(c.cnt);
                }
            }else{
                if (nowPage == 1) {
                    $("#tradeListBox").html(c.cnt);
                }else{
                    $('#tradeListBox').append('<div class="dropload-noData"><em class="no-data"></em>暂无更多数据</div>');
                }
                myApp.detachInfiniteScroll($tradeContainer);
            }

			loadLazyImage("#tradeListBox");
        },
        complete: function(xhr, type) {
            tradePageLoad = false;
            $tradeContainer.find('.infinite-scroll-preloader').hide();
        },
        error: function(xhr, type) {
            myApp.detachInfiniteScroll($tradeContainer);
        }
    });
}
var height = $(window).height();
function loadLazyImage(classSelecter){

	$.each($(classSelecter +" .lazy"),function(k,v){		   
		var toTop = $(v).offset().top;
		if(toTop > height){
			return true;
		}
		if($(v).hasClass('isLazying')){
			return true;
		}
		$(v).addClass('isLazying');
		var trueUrl = $(v).attr('data-src');
		var imgObject = new Image();
		imgObject.src = trueUrl;
		imgObject.targetObject = $(v);
		imgObject.onload = function(e){
			if(imgObject.targetObject.hasClass('preload')){
				imgObject.targetObject.removeClass('lazy').removeClass('isLazying').addClass('lazyed');
				return;
			}
			if(imgObject.targetObject.attr('lazyType') == 'background'){
				imgObject.targetObject.css('background-image','url(' + imgObject.src + ')').removeClass('lazy').removeClass('isLazying').addClass('lazyed');
			}else{
				imgObject.targetObject.attr('src',imgObject.src).removeClass('lazy').removeClass('isLazying').addClass('lazyed');
			}
		}
	})
}


function loadMyGoods(){
    $myContainer.find('.infinite-scroll-preloader').show();

    var nowPage = 0
        ,type = $('.changeMylist.active').attr('pageId'),
        divBox = '#my-sell';
    if(type == 'audit') divBox = '#my-audit';

    nowPage = $(divBox).attr('nowPage');
    nowPage++;
    $.ajax({
        type: 'GET',
        url: '/userCenter/getMyGoods?page=' + nowPage,
        dataType: 'json',
        data: {
            format: 'json',
            companyId: config.manageUid,
            platform: platform,
            authToken: authToken,
            deviceId: config.deviceId,
            productCode: config.productCode,
            type: type
        },
        success: function(data) {
            try {
                var c = data.data;
            } catch (e) {
                return;
            }

            $(divBox).attr('nowPage', nowPage);

            if(c.rows > 0){
                if (nowPage == 1) {
                    $(divBox).html(c.cnt);
                } else {
                    $(divBox).append(c.cnt);
                }
            }else{
                if (nowPage == 1) {
                    $(divBox).html(c.cnt);
                }else{
                    if($(divBox).find('.dropload-noData').length == 0){
                        $(divBox).append('<div class="dropload-noData"><em class="no-data"></em>暂无更多数据</div>');
                    }
                }
                myApp.detachInfiniteScroll($myContainer);
            }

            $('#orderListBox > div').hide();
            $(divBox).show();
			
			setTimeout(function(){
				loadLazyImage(".orderListBox");
			},50);
        },
        complete: function(xhr, type) {
            myInfoPageLoad = false;
            $myContainer.find('.infinite-scroll-preloader').hide();
        },
        error: function(xhr, type) {
            // myApp.detachInfiniteScroll($myContainer);
        }
    });
}

function switchToGoods(){
    if(!$('.changeView').eq(0).hasClass('active')){
        $(".changeView").removeClass('active');
        $('.changeView').eq(0).addClass('active');
        $('#info').hide();
        $('#my').hide();
        $("#goodsList").show();
    }
}
function switchToMyGoods(forceFresh){
    if( forceFresh || (!$('.changeView').eq(2).hasClass('active') && !$('.changeMylist').eq(1).hasClass('active')) ){
        $(".changeView").removeClass('active');
        $('.changeView').eq(2).addClass('active');
        $('#info').hide();
        $('#goodsList').hide();
        $("#my").show();
        $(".changeMylist").removeClass('active');
        $('.changeMylist').eq(1).addClass('active');
        // $('#orderListBox').html('');
        // $('#my .loadBox').show();
        $('#my').attr("nowPage","0");
        $('#my-sell').attr('nowPage', 0);
        myApp.attachInfiniteScroll($myContainer);
        mainView.router.back();
        loadOrderList();
    }
}

!function(){
    loadGoodsList();
    // loadOrderList();
    // loadTradeList();

    $sortModaLayer = $('#sortModaLayer');
    $(document).on('click', '#sortModaLayer', closeSortModa);
    $(document).on('click','.sortButton',function(){
        var boxid = $(this).attr('boxId');
        if($sortModaLayer.css('display') == 'none'){
            $sortModaLayer.show();
            setTimeout(function(){
                $sortModaLayer.css('opacity','1');
                $(`#${boxid}`).addClass('sortBoxIn');
            },20)
        }else{
            closeSortModa();
        }
    });
    
    $(document).on('click',".sortItem.goodsItem",function(){
        $goodsContainer.attr('nowPage', 0);
        // $myContainer.attr('nowPage', 0);
        // $tradeContainer.attr('nowPage', 0);
        sortType = $(this).attr('attr');
        switchToGoods();

        closeSortModa();
        loadGoodsList();
        // loadTradeList();

    })

    $(document).on('click',".searchIcon",function(){
        kw = $('#kw').val();
        $goodsContainer.attr('nowPage', 0);
        switchToGoods();
        loadGoodsList();
    });
    $(document).on('keyup',"#kw",function(){
        kw = $('#kw').val();
        $goodsContainer.attr('nowPage', 0);
        switchToGoods();
        loadGoodsList();
    });
    $(document).on('keyup',"#kw-game",function(){
        kwgame = $(this).val();
        $("#gamelListId li.gameItem").each(function(k,v){
        	var productname=$(v).attr("name");
    		if(productname.toLowerCase().indexOf(kwgame.toLowerCase()) != -1){
    			$(v).show();
    		}else{
    			$(v).hide();
    		}
        })
        
    });

    var goodsPage = $goodsContainer;

    goodsPage.on('infinite', function() {
        if (goodsPageLoad) return;
        goodsPageLoad = true;
        // goodsPage.find('.infinite-scroll-preloader').show();
        loadGoodsList();
    });
    $myContainer.on('infinite', function() {
        if (myInfoPageLoad) return;
        myInfoPageLoad = true;
        reloadFinish = true;
        reloadSell = true;
        reloadAudit = true;
        reloadPay = true;
        // $myContainer.find('.infinite-scroll-preloader').show();
        loadOrderList();
    });
    $tradeContainer.on('infinite', function() {
        if (tradePageLoad) return;
        tradePageLoad = true;
        // $tradeContainer.find('.infinite-scroll-preloader').show();
        loadTradeList();
    });
    $(document).on('pageAfterAnimation', '.page[data-page=index]', function (e) {
    	$('#goodsList').attr("nowPage","0");
    	$('#my').attr("nowPage","0");
        $('#info').attr("nowPage","0");

        if($('.goodsListBox .goodsBox').length == 0 || reloadGoods == true){
        	loadGoodsList();
            reloadGoods = false;
        }

    });

    $(document).on('click','.changeMylist',function(){
        if($(this).hasClass('active')){
            return false;
        }
        var pageId = $(this).attr('pageId');
        closeSortModa();
        $(".changeMylist").removeClass('active');
        $(this).addClass('active');
        $('#orderListBox > div').hide();
        // $('#my .loadBox').show();
        $('#my').attr("nowPage","0");

        myApp.attachInfiniteScroll($myContainer);
        if(pageId == 'payed'){
            if($('#my-finish .dropload-noData').length > 0){
                reloadFinish = false;
                myApp.detachInfiniteScroll($myContainer);
            }
        }
        if(pageId == 'sell'){
            if($('#my-sell .dropload-noData').length > 0){
                reloadSell = false;
                myApp.detachInfiniteScroll($myContainer);
            }
        }
        if(pageId == 'audit'){
            if($('#my-audit .dropload-noData').length > 0){
                reloadAudit = false;
                myApp.detachInfiniteScroll($myContainer);
            }
        }
        if(pageId == 'waitPage'){
            if($('#my-pay .dropload-noData').length > 0){
                reloadPay = false;
                myApp.detachInfiniteScroll($myContainer);
            }
        }
        loadOrderList();
		setTimeout(function(){
			loadLazyImage(".orderListBox");
		},50);
    })
}()

$(".orderListBox").scroll(function(){
	loadLazyImage(".orderListBox");
})

/**
 * 商品详情
 */
var checkSiteOpen,
    goodsData,
    goodsSwiper = null,
    goodsBrowser = null;
!function(){
    var $goodsDetailDom = $('.page[data-page=goodsDetail]')

    $(document).on('click','.openGoodsDetail',function(){
        goodsId = $(this).attr('goodsId');

        $goodsDetailDom.html('')
        goodsData = {};
		pushHistory('page');
        mainView.router.load({pageName: 'goodsDetail'});
    })

    $(document).on('click','#goods-slider .swiper-slide',function(){

        var list = new Array(), initIndex = 0, clickIndex = $(this).attr('num'), runIndex = 0;
        $('#goods-slider .swiper-slide').each(function(k,v){
        	list.push($(v).attr('data-src'));
            if( clickIndex == $(v).attr('num')  ){
                initIndex = runIndex;
            }
            runIndex++;
        })

        goodsBrowser = myApp.photoBrowser({
            photos : list,
            backLinkText: '关闭',
            theme: 'dark',
            toolbar:false,
            ofText:'/',
            navbarTemplate:photeNavTemp(),
            initialSlide:initIndex,
			minZoom:0.1,
            onOpen:function(){
                photoOpen = 1;
            },
            onClose:function(){
                photoOpen = 0;
            },
            onSlideChangeStart:function(){
                // 
            },
        });
        if(goodsBrowser != null){
			pushHistory('page');
            goodsBrowser.open();
        }
    })

    $(document).on('pageAfterAnimation', '.page[data-page=goodsDetail]', function (e) {
        if($goodsDetailDom.text().length < 10){
            getGoodsDetail(goodsId)
        }
    })

    function getGoodsDetail(goodsId){
        myApp.showIndicator();

        $.ajax({
            type: 'GET',
            url: '/userCenter/getGoodDetail?id=' + goodsId,
            dataType: 'json',
            data: {
                format: 'json',
                companyId: config.manageUid,
                platform: platform,
                authToken: authToken,
                deviceId: config.deviceId,
                productCode: config.productCode,
            },
            success: function(data) {
                var r, html,
                    imageDom = '',
                    gameDom = '',
                    serverDom = '',
                    vipDom = '',
                    userDom = '';
                try{
                    r = data.data
                    goodsData = r;
                }catch(e){
                    layer.open({content: '获取商品信息失败！',skin: 'msg',time: 4,anim:false });
                    mainView.router.back();
                    return;
                }


                if(typeof r.gameData == 'object'){
                    gameDom = `
                        <div class="priceInfo mt10">
                            <div class="gameShowIcon openGameDetail" data-pid="${r.gameData.id}" data-pcode="${r.gameData.productCode}">
                                <img src="${r.gameData.logo}">
                                <div class="gameTitleShow">
                                    <div class="font16">${r.gameData.showName}</div>
                                    <div class="font14 gray">${r.gameData.summary}</div>
                                </div>
                                <div class="gameToButton"><i class="toDetailIcon"></i></div>
                            </div>
                        </div>
                    `
                }
                if(r.images.length > 0){
					var imageHeight = config.theme == 'weixin' ? 450 : 250;
					var defaultHegith = imageHeight + config.statusBarHeight;
					
                    imageDom = `
                        <div id="goods-slider" class="swiper-container" style="height:${defaultHegith}px;">
                            <div class="swiper-wrapper">
                    `
                    for(var i in r.images){
                        imageDom = `
                            ${imageDom}<div class="swiper-slide" style="background:url(${r.images[i]}) no-repeat 50% 50%;background-size: cover;" num="${i}" data-src="${r.images[i]}"></div>
                        `
                    }
                    imageDom = `${imageDom}</div>
                            <div class="swiper-pagination"></div>
                        </div>`
                }else{
                    imageDom = `
                        <div class="gameInfo" id="gameScreenshot" style="height: 52px;"><a href="#" class="detailbackButton back link"></a></div>
                    `
                }
                if(r.serverName != ''){
                    serverDom = `
                        <div class="lineText mt10">
                            <div class="left">所在区服</div>
                            <div class="right">${r.serverName}</div>
                        </div>
                    `
                }
                if(r.vipLevel != ''){
                    vipDom = `
                        <div class="lineText">
                            <div class="left">VIP等级</div>
                            <div class="right">${r.vipLevel}</div>
                        </div>
                    `
                }
                if(typeof r.user == 'object'){
                    userDom = `
                        <div class="lineText mb10">
                            <div class="left">账号创建</div>
                            <div class="right">${r.user.regTime}</div>
                        </div>
                    `
                }
                var butBtnClass='enable';
                if(r.hadOrder == 1 || r.isSelf == 1){
                	butBtnClass = 'disable disabled';
                }
                if(r.nowStatus >= 2){
                    butBtnClass = 'disable disabled';
                }

                html = `
                    <div class="detailBoy">
                        <a href="#" class="detailbackButton back link" style="z-index:2;"></a>
                        ${imageDom}

                        <div class="priceInfo">
                            <div class="showPrice">￥ ${r.price}</div>
                            <div class="showTitle">${r.goodsName}</div>
                            <div class="showDesc">${r.desc}</div>
                        </div>

                        ${gameDom}
                        ${serverDom}
                        
                        <div class="lineText">
                            <div class="left">发布时间</div>
                            <div class="right">${r.timeSay}</div>
                        </div>
                        <div class="lineText">
                            <div class="left">累计充值</div>
                            <div class="right">￥${r.allPayAmount}</div>
                        </div>
                        ${vipDom}
                    <!--    <div class="lineText borderBottomNone">
                            <div class="left">交易次数</div>
                            <div class="right">0次</div>
                        </div> -->
                        ${userDom}
                        
                    </div>

                    <div class="goodsBuy">
                        <div class="left">
                            <div class="buyIconArea kefu" data-pid="${r.gameData.id}" data-pcode="${r.gameData.productCode}">
                                <i class="serviceIcon"></i>
                                <p>客服</p>
                            </div>
                            <div class="buyIconArea price" data-pid="${r.gameData.id}" data-pcode="${r.gameData.productCode}">
                                <i class="saleIcon"></i>
                                <p>还价</p>
                            </div>
                        </div>
                        <div class="right"><a class="buyButton ${butBtnClass}" data-goodsid="${goodsId}" href="javascript:;">立即购买</a></div>
                    </div>
                `

                $goodsDetailDom.html(`<div class="page-content infinite-scroll" style="padding:0; overflow:hidden">${html}</div>`);

                goodsSwiper = myApp.swiper('#goods-slider', {
                    pagination:'.swiper-pagination',
                    lazyLoading: true,
                    speed: 400
                    // autoplay: {delay: 2000}
                });
            },
            complete: function() {
                myApp.hideIndicator();
            },
            error: function() {
                layer.open({content: '获取商品信息失败！',skin: 'msg',time: 4,anim:false });
                mainView.router.back();
            }
        });
    }

    var gameId = 0, gameCode = '';
    $(document).on('click', '.openGameDetail', function(){
        gameId = $(this).data('pid');
        gameCode = $(this).data('pcode');

        var jumpUrl = `/userCenter/gameDetail?statusBarHeight=${config.statusBarHeight}&productId=${gameId}&isFromUserCenter=1&screenType=${screenType}&authToken=${authToken}&deviceId=${config.deviceId}&productCode=${gameCode}&channelCode=`;

        myApp.showIndicator();
        myApp.popup('#siteFrameLayer');
        setTimeout(function(){
            $('#siteFrame').attr('src', jumpUrl);
        },400);
        checkSiteOpen = setTimeout(function(){
            //30秒后
            if($(".preloader-indicator-modal").length > 0){
                myApp.hideIndicator();
                $(".preloader-indicator-modal").css('margin-left','0px');
                websiteClose();
                layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
            }
        },15000);
        $(".preloader-indicator-modal").css('margin-left','-25px');
    });
    $(document).on('click', '.closeGameDetail', function() {
        return myApp.closeModal("#gameDetail");
    });

}()

/**
 * 客服
 */
var isSupportService;
!function(){
    var productId = 0
        ,productCode = ''
        ,thisQATitle = '客服联系方式'
        ,isUseFull
        ,isUnUseFull

    isSupportService = doActionFunction('isSupportService',{});
    if(isSupportService == '1'){
        return true;
    }

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

    $(document).on('click','.buyIconArea.kefu',function(){
        productId = $(this).data('pid');
        productCode = $(this).data('pcode');

        $("#qaDetailContent").html('');
        $(".useFull").removeClass('useFullOn');
        $(".unUseFull").removeClass('unUseFullOn');

        isUseFull = getUserFul(thisQATitle);
        if(isUseFull){
            $(".useFull").addClass('useFullOn');
            $(".unUseFull").removeClass('unUseFullOn');
        }

        isUnUseFull = getUnUserFul(thisQATitle);
        if(isUnUseFull){
            $(".useFull").removeClass('useFullOn');
            $(".unUseFull").addClass('unUseFullOn');
        }
		pushHistory('page');
        mainView.router.load({pageName: 'qaDetail'});
    });

    $(document).on('pageAfterAnimation', '.page[data-page=qaDetail]', function (e) {
        myApp.showIndicator();
        
        $.ajax({
            type: 'POST',
            url: '/bbs/customerService',
            dataType: 'json',
            data:{format:'json',productId:config.productId,authToken:authToken,deviceId:config.deviceId,channelCode:config.channelCode,productCode:config.productCode},
            success: function(data){
                if(data.status == true){
                    var thisHtml = data.data.serviceInfo;
                    thisHtml = thisHtml.replace(/\|/g,"<br>");
                    $("#qaDetailContent").html(thisHtml);

                    if(data.data.useServiceCenter == 2){
                        openServiceIM();
                        $('.qaDetailContent').html('正在打开在线客服...');
    					setTimeout(()=>myApp.getCurrentView().router.back(), 400);
                    }
                }
            },
            complete:function(){
                myApp.hideIndicator();
            }
        });
    });

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

    });
}()
function openServiceIM(){

	var uid = config.deviceId;
	var username = 'none';
	var nickName = '游客';
	try{
		if(userData){
			uid = userData.uid;
			nickName = userData.nickName;
			username = userData.username;
		}else{
			uid = config.deviceId;
		}
	}catch(e){
	}
	
	
	var params = new Object();
	params.action = 'showServiceIM';
	params.data = {productCode:config.productCode,username:username,uid:uid.toString(),deviceId:config.deviceId,roleName:nickName,roleBalance:'',roleId:'',roleParty:'',roleServer:'',vipLevel:''};
	return doActionFunction(params.action,params.data);
}


/**
 * 发布
 */
var imgArray = [],
    productId = 0,
    userId = 0;
function chkSubmitForm(){
    var f = {
        productId: productId,
        userId: userId,
        title: $('#sub-title').val().trim(),
        serverName: $('#sub-server').val().trim(),
        level: parseInt($('#sub-level').val()),
        price: parseFloat($('#sub-price').val()),
        text: $('#sub-text').val().trim()
    },
    stat = (f.productId > 0) && (f.userId > 0) && (f.title != '') && (f.serverName != '') && (!isNaN(f.level) && f.level > 0) && (!isNaN(f.price) && f.price > 0) && (f.text != '');
    stat = stat && (imgArray.length > 0);
    if(stat){
        $('.submit.sub-account').removeClass('disabled');
    }else{
        $('.submit.sub-account').addClass('disabled');
    }
}
function resetSubmitForm(){
    productId = 0;
    userId = 0;
    imgArray = [];
    $('.page[data-page="sellPage"] input').val('');
    $('.page[data-page="sellPage"] textarea').val('');
    $('input[name=selectGameRadio]').prop('checked', false);
    $('input[name=selectUserRadio]').prop('checked', false);
    $('.showUserName').text('选择交易账号');
    $('.showGameName').text('选择所属游戏');
    $("#showAccountImage .img-items").remove();
    $('.submit.sub-account').addClass('disabled');
}
!function(){
  $(document).on("click",".backToPrev",function(){
    if($(this).data('fresh') == 'false'){
        reloadGoods = false;
    }
	if($(this).data('freshSell') == 'false'){
		referSellPage = false;
	}
    myApp.hideIndicator();
    mainView.router.back();
  })

  var accountList = {},
      item = {},
      userCount = $('#userBoxTpl .userItem').length;

    $(document).on('click','.openSubmitForm',function(){
		referSellPage = true;
        
        userCount = $('#userBoxTpl .userItem').length;
        if(userCount == 0){
            $('#sell-form-container').html('<div class="centerNoMessage"><img src="/static/userCenter/img/nonotice.png"><p>没有可出售账号</p></div>');
        }else{

            resetSubmitForm();
        }
        
        sellAction = 'add';
		pushHistory('page');
        mainView.router.load({pageName: 'sellPage'});
    });

    $(document).on('keyup', '#sub-title,#sub-server,#sub-level,#sub-price,#sub-text', chkSubmitForm);

    // 筛选游戏
    $(document).on('click',".gameItem",function(){
        if(productId == $(this).attr('pid')){
            mainView.router.back();
            return true;
        }

        var $tpl = $('#userBoxTpl'),
            uid;
        uids = JSON.parse($(this).data('ulist'));

        $tpl.find('li').removeClass('disabled');
        $tpl.find('li .item-after').show();

        $tpl.find('li').each(function(i, v){
            uid = parseInt($(v).attr('uid'));
            if(uids.indexOf(uid) >= 0){
                $(v).removeClass('disabled');
                $(v).find('.item-after').hide();
            }else{
                $(v).addClass('disabled');
                $(v).find('.item-after').show();
            }
        });
        $('#userBox').html($tpl.html());

        productId = $(this).attr('pid');
        userId = 0;
        $('.showUserName').text('选择交易账号');

        $('.showGameName').text($(this).attr('name'));
        chkSubmitForm();
        mainView.router.back();

        
    });

    // 筛选账号
    $(document).on('click',".userItem",function(){
        userId = parseInt($(this).attr('uid'));
        var username = $(this).attr('name');

        myApp.showIndicator();
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/userCenter/checkAccount',
            data: {
                format: 'json',
                companyId: config.manageUid,
                platform: platform,
                authToken: authToken,
                deviceId: config.deviceId,
                productCode: config.productCode,
                userId: userId
            },
            success: function(response){
                if(true != response.status){
                    return layer.open({content: response.message,skin: 'msg',time: 3,anim:false });
                }

                if(response.data.count == 0){
                    $('.showUserName').text(username);
                    console.log(productId, userId);
                    chkSubmitForm();
                    mainView.router.back();
                    return true;
                }

                myApp.confirm(`此账号已绑定${response.data.count}个子账号，是否同时出售？`, '提示', function () {
                    $('.showUserName').text(username);
                    console.log(productId, userId);
                    chkSubmitForm();
                    mainView.router.back();
                });
            },
            timeout:900000,
            complete: function(){
                myApp.hideIndicator();
            },
            error: function(s){
                return layer.open({content: '网络错误，请刷新重试！',skin: 'msg',time: 3,anim:false });
            }
        });
        
    })
    $(document).on('click','.fastSearchUser',function(){
        var key = $(this).find('div').text();
        var offsetNum = -1;
        var isFristFind = false;
        $.each($(".userItem"),function(k,v){
            offsetNum++;
            if($(v).attr('code') == key && !isFristFind){
                var toTop = offsetNum*44;
                $("#searchUserBox").scrollTop(toTop);
                isFristFind = true;
                return false;
            }
        })
    })

  var referSellPage = true;
  $(document).on('pageAfterAnimation', '.page[data-page=sellPage]', function (e) {
    myApp.hideIndicator();
	if(referSellPage == false){
		return;
	}
	
  })


  $(document).on('click','.selectImageBack',function(){
        var type = $(this).attr('type');
        var params = {type:type,rate:50,maxSelectNum:15};
        doActionFunction('selectImage',params);
        myApp.showIndicator();
    })

  $(document).on('click', '.submit.sub-account', function(){

    var $btn = $(this),
        formData = {
          format: 'json',
          companyId: config.manageUid,
          platform: platform,
          authToken: authToken,
          deviceId: config.deviceId,
          productCode: config.productCode,
          productId: productId,
          userId: userId,
          title: $('#sub-title').val().trim(),
          serverName: $('#sub-server').val().trim(),
          level: parseInt($('#sub-level').val()),
          price: parseFloat($('#sub-price').val()),
          text: $('#sub-text').val().trim(),
          imgList: imgArray.join(','),
        }

    if(formData.productId < 1){
      layer.open({content: '请选择游戏',skin: 'msg',time: 2,anim:false });
      return;
    }
    if(formData.userId < 1){
      layer.open({content: '请选择账号',skin: 'msg',time: 2,anim:false });
      return;
    }
    if(formData.title == ''){
      layer.open({content: '请填写交易名称',skin: 'msg',time: 2,anim:false });
      return;
    }
    if(formData.serverName == ''){
      layer.open({content: '请填写区服',skin: 'msg',time: 2,anim:false });
      return;
    }
    if(isNaN(formData.level) || formData.level < 1){
      layer.open({content: '请如实填写VIP等级',skin: 'msg',time: 2,anim:false });
      return;
    }
    if(isNaN(formData.price) || formData.price <= 0){
      layer.open({content: '请填写售价',skin: 'msg',time: 2,anim:false });
      return;
    }
    if(formData.text == ''){
      layer.open({content: '请填写账号描述',skin: 'msg',time: 2,anim:false });
      return;
    }
    if(imgArray.length == 0){
      layer.open({content: '请上传至少一张截图',skin: 'msg',time: 2,anim:false });
      return;
    }

    $btn.addClass('disabled');
    myApp.showIndicator();
    $.ajax({
      type: 'POST',
      dataType: 'json',
      url: '/userCenter/sellAccount',
      data: formData,
      success: function(response){

        if(true != response.status){
            return layer.open({content: response.message,skin: 'msg',time: 3,anim:false });
        }

        $(`.userItem.item${userId}`).remove();

        resetSubmitForm();
        reloadSell = true;
        switchToMyGoods(true);
        $goodsContainer.attr('nowPage', 0);
        // switchToGoods();
        loadGoodsList();
        /*reloadGoods = true;
        layer.open({content: '已成功发布交易！',skin: 'msg',time: 3,anim:false });
        setTimeout(function(){
            resetSubmitForm();
            mainView.router.back();
        }, 3000);*/

      },
      timeout:900000,
      complete: function(){
        $btn.removeClass('disabled');
        myApp.hideIndicator();
      },
      error: function(s){
        return layer.open({content: '发布失败网络超时',skin: 'msg',time: 3,anim:false });
      }
    })

  })

}()
/**
 * 确定购买账号下单页面
 */
!function(){
    var $goodsPayDom = $('.page[data-page=payNowPage]')

    $(document).on('click','.buyButton.enable',function(){
        goodsId = $(this).data('goodsid');


        $('.paypage-goodsinfo .goodsIcon img').attr("src",'/static/userCenter/img/noGameShot.png');
		pushHistory('page');
        mainView.router.load({pageName: 'payNowPage'});
    })

    $(document).on('pageAfterAnimation', '.page[data-page=payNowPage]', function (e) {
        getGoodsPayPage(goodsId)
    })

    function getGoodsPayPage(goodsId){
        // myApp.showIndicator();
    	
    	if(!config.payments){
    		var formData = {
	          companyId: config.manageUid,
	          authToken: authToken,
	          deviceId: config.deviceId,
	          productCode: config.productCode,
	          productId: productId
	        }
    		$.ajax({
		      type: 'POST',
		      dataType: 'json',
		      url: '/userCenter/getPayment',
		      data: formData,
		      success: function(response){

		        if(true != response.status){
		        	mainView.router.back();
		            layer.open({content: response.message,skin: 'msg',time: 3,anim:false });
		            return ;
		        }
		        config.payments = response.data;
		        var payHtml = "";
		        $.each(response.data,function(k,v){
		        	if(config.theme=="weixin" && v.payTypeId!=88){
		        		return false;
		        	}
		        	var alinkClass = 'enable';
		        	if(v.payTypeId==173 && goodsData.price*1>v.userAmount){
		        		alinkClass= 'disable';
		        	}
		        	if(v.payTypeId==173){
		        		config.userWalletAmount= v.userAmount;
		        	}
		        	payHtml +=`<a href="javascript:;" class="${(k==0 && alinkClass=='enable')? 'selected' : ''} ${alinkClass}">
									<em></em>
									<img src="/static/images/pay_type_${v.payLogoName}.png" alt="">
									<span class="pay-type-name">${v.payName}${(v.payTypeId == 173)?`(￥${v.userAmount})`:''}<i class="rebate" id="reb${v.payTypeId}"></i></span>
									<input type="radio" name="paytype" class="paytype-radio" value="${v.payTypeId}" ${(k==0 && alinkClass=='enable') ? 'checked="checked"' : ''}>
								</a>`;
		        });
		        $("#pay-type-items").html(payHtml);

		      },
		      timeout:900000,
		      complete: function(){
		        myApp.hideIndicator();
		      },
		      error: function(s){
		    	  layer.open({content: '发布失败网络超时',skin: 'msg',time: 3,anim:false });
		    	  mainView.router.back();
		    	  return;
		      }
		    })
    	}else{
    		var payHtml = "";
	        $.each(config.payments,function(k,v){
	        	if(config.theme=="weixin" && v.payTypeId!=88){
	        		return false;
	        	}
	        	var alinkClass = 'enable';
	        	if(v.payTypeId==173 && goodsData.price*1>v.userAmount){
	        		alinkClass= 'disable';
	        	}
	        	payHtml +=`<a href="javascript:;" class="${(k==0 && alinkClass=='enable')? 'selected' : ''} ${alinkClass}">
								<em></em>
								<img src="/static/images/pay_type_${v.payLogoName}.png" alt="">
								<span class="pay-type-name">${v.payName}${(v.payTypeId == 173)?`(￥${v.userAmount})`:''}<i class="rebate" id="reb${v.payTypeId}"></i></span>
								<input type="radio" name="paytype" class="paytype-radio" value="${v.payTypeId}" ${(k==0 && alinkClass=='enable') ? 'checked="checked"' : ''}>
							</a>`;
	        });
	        $("#pay-type-items").html(payHtml);
    	}

        if(!goodsData.hasOwnProperty('id')){
            layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
            mainView.router.back();
            return;
        }
        $('.paypage-goodsinfo .goodsIcon img').attr("src",goodsData.img);

        var vip_server_txt='';
        if(goodsData.vipLevel && goodsData.serverName){
            vip_server_txt = "VIP"+goodsData.vipLevel+" / "+goodsData.serverName;
        }else if(goodsData.vipLevel){
            vip_server_txt = "VIP"+goodsData.vipLevel;
        }else if(goodsData.serverName){
            vip_server_txt = goodsData.serverName;
        }
        $(".paypage-goodsinfo .goodsServer").html(vip_server_txt);
        $(".paypage-goodsinfo .goodsGame").html(goodsData.goodsName);
        $("#pay-money").html(goodsData.price);
        $("#pay-goodsid").val(goodsData.id);
    }

    $(document).on("click","#pay-type-items a.enable",function(e){
        $("#pay-type-items a").removeClass("selected");
        $(this).addClass("selected");
        $(this).find("input[name='paytype']").prop("checked","checked");
    });
    $(document).on('click', '.submit.pay-btn', function(){
        var $btn = $(this);
        var formUrl = $("#payform").prop("action");
        var formData ={
            "companyId": config.manageUid,
            "goodsId":$("#pay-goodsid").val(),
            "authToken":authToken,
            "productCode":config.productCode,
            "channelCode":config.channelCode,
            "deviceId":config.deviceId,
            "payType":$(".paytype-radio:checked").val()
        };
        //余额支付弹出支付密码
        if(formData.payType == 173 && !config.userPayPass){
        	doActionFunction("doInputPayPass",{amount:goodsData.price,rePay:0,userWalletAmount:config.userWalletAmount})
        	return false;
        }else if(formData.payType == 173 && config.userPayPass){
        	formData.payPass = config.userPayPass;
        }
        try{
        	if(config.theme == "weixin"){
        		formData.openid = config.openId;
        		formData.theme = "weixin";
        	}
        }catch(e){}
        config.userPayPass = ''; //重置用户输入的密码
        $btn.addClass('disabled');
        myApp.showIndicator();
        $.ajax({
          type: 'POST',
          dataType: 'json',
          url: formUrl,
          data: formData,
          success: function(response){
            $('#my-finish').attr('nowPage', 0);
            $('#my-sell').attr('nowPage', 0);
            $('#my-audit').attr('nowPage', 0);
            $('#my-pay').attr('nowPage', 0);
            $goodsContainer.attr('nowPage', 0);
            reloadGoods = true;
            reloadFinish = true;
            reloadSell = true;
            reloadAudit = true;
            reloadPay = true;
            loadGoodsList();
            if(response.status==false){
                layer.open({content: response.message,skin: 'msg',time: 4,anim:false });
                return false;
            }else{
            	config.payParams=response.data;
				pushHistory('page');
                mainView.router.load({pageName: 'payWaitPage'});
            }
          },
          timeout:900000,
          complete: function(){
            $btn.removeClass('disabled');
            myApp.hideIndicator();
          },
          error: function(s){
            return alertMsg('购买失败网络超时');
          }
        })

      });
    $(document).on('pageInit', '.page[data-page="payWaitPage"]', function (e) {
    	payNowClient(config.payParams);
	});
	$(document).on('pageReinit', '.page[data-page="payWaitPage"]', function (e) {
    	payNowClient(config.payParams);
	});
    function payNowClient(payData){
        myApp.showIndicator();
        try{
        	if(payData.hasOwnProperty('payParams')){
            	if(payData.payParams){
            		myApp.hideIndicator();
            		if(config.theme=="weixin"){
            			setTimeout(function(){
            				checkWxJs(payData.payParams);
            			},400);
            			return true;
            		}
                    doActionFunction("dopay",payData)
                    return true;
            	}
            }
        }catch(e){}
        var formData ={
            "companyId": config.manageUid,
            "authToken":authToken,
            "productCode":config.productCode,
            "channelCode":config.channelCode,
            "deviceId":config.deviceId,
            "orderNo":payData.orderNo,
        };
        $.ajax({
          type: 'POST',
          dataType: 'json',
          url: "/userCenter/getOrder",
          data: formData,
          success: function(response){
            if(response.status==false){
            	myApp.hideIndicator();
                $("#order-msg").html(response.message);
                return false;
            }else{
                //重新创建支付链接
            	var formData ={
                    "companyId": config.manageUid,
                    "goodsId":response.data.siteGoodsId,
                    "authToken":authToken,
                    "productCode":config.productCode,
                    "channelCode":config.channelCode,
                    "deviceId":config.deviceId,
                    "payType":response.data.payType
                };
            	config.userWalletAmount=response.data.userWalletAmount;
                //余额支付弹出支付密码
                if(formData.payType == 173 && !config.userPayPass){
                	doActionFunction("doInputPayPass",{amount:response.data.amount,rePay:1,formData:formData,userWalletAmount:config.userWalletAmount})
                	return false;
                }
                try{
                	if(config.theme == "weixin"){
                		formData.openid = config.openId;
                		formData.theme = "weixin";
                	}
                }catch(e){}
                config.userPayPass = ''; //重置用户输入的密码
                $.ajax({
                  type: 'POST',
                  dataType: 'json',
                  url: "/userCenter/createOrder",
                  data: formData,
                  success: function(res){
                    $('#my-finish').attr('nowPage', 0);
                    $('#my-sell').attr('nowPage', 0);
                    $('#my-audit').attr('nowPage', 0);
                    $('#my-pay').attr('nowPage', 0);
                    $goodsContainer.attr('nowPage', 0);
                    reloadGoods = true;
                    reloadFinish = true;
                    reloadSell = true;
                    reloadAudit = true;
                    reloadPay = true;
                    loadGoodsList();
                    if(res.status==false){
                        $("#order-msg").html(res.message);
                        return false;
                    }else{
                    	try{
                        	if(config.theme == "weixin"){
                        		checkWxJs(res.data.payParams);
                    			return true;
                        	}
                        }catch(e){}
                        doActionFunction("dopay",res.data)
                        return false;
                    }
                  },
                  timeout:900000,
                  complete: function(){
                    myApp.hideIndicator();
                  },
                  error: function(s){
                    $("#order-msg").html("购买失败网络超时");
                  }
                });
            }
          },
          timeout:900000,
          complete: function(){
            myApp.hideIndicator();
          },
          error: function(s){
        	  myApp.hideIndicator();
              $("#order-msg").html("获取订单信息失败，网络超时");
          }
        })
    }
    $(document).on('click', '#backGoodsList', function(){
        $('#goodsList').attr("nowPage","0");
        $('#my').attr("nowPage","0");
		$goodsContainer.attr('nowPage','0');
		loadGoodsList();
        $('#my-finish').attr('nowPage', 0);
        $('#my-sell').attr('nowPage', 0);
        $('#my-audit').attr('nowPage', 0);
        $('#my-pay').attr('nowPage', 0);
        reloadGoods = true;
        reloadFinish = true;
        reloadSell = true;
        reloadAudit = true;
        reloadPay = true;
        myApp.hideIndicator();
		pushHistory('page');
        mainView.router.load({pageName: 'index'});
    });
    $(document).on('click', '#backMyOrder', function(){
        myApp.hideIndicator();
		pushHistory('page');
        mainView.router.load({pageName: 'index'});
    });
    $(document).on('click', '.paynow-link', function(){
        var $btn = $(this);
        var payData={
            orderNo:$btn.data("orderno")
        };
        config.payParams = payData;
		pushHistory('page');
		$("#order-msg").html('正在检查支付...');
        mainView.router.load({pageName: 'payWaitPage'});
    });
}()
function alertMsg(msg){

	$(".deleteLayer").css('visibility','visible').css('display','block').css('opacity',1);
	var $thisImage = $(this);
	delImage = myApp.modal({
		title:  '提示',
		text: msg,
		
		buttons: [{
			text: '确定',
			onClick:function(){
                myApp.closeModal(delImage);
				$(".deleteLayer").css('visibility','hidden').css('display','none').css('opacity',0);
            }
		}]
	});									
}

function payByEwallet(params){
	//重新创建支付链接
	var formData =params.formData;
	formData.payPass = config.userPayPass;
    config.userPayPass = ''; //重置用户输入的密码
    $.ajax({
      type: 'POST',
      dataType: 'json',
      url: "/userCenter/createOrder",
      data: formData,
      success: function(res){
        $('#my-finish').attr('nowPage', 0);
        $('#my-sell').attr('nowPage', 0);
        $('#my-audit').attr('nowPage', 0);
        $('#my-pay').attr('nowPage', 0);
        $goodsContainer.attr('nowPage', 0);
        reloadGoods = true;
        reloadFinish = true;
        reloadSell = true;
        reloadAudit = true;
        reloadPay = true;
        loadGoodsList();
        if(res.status==false){
            //layer.open({content: res.message,skin: 'msg',time: 4,anim:false });
            alertMsg(res.message);
            return false;
        }else{
        	$("#order-msg").html("支付成功");
			window.parent.postMessage({"action":"referAmount"},"*");
        }
      },
      timeout:900000,
      complete: function(){
    	  myApp.hideIndicator();
      },
      error: function(s){
        return layer.open({content: '购买失败网络超时',skin: 'msg',time: 3,anim:false });
      }
    });
}


/**
 * 重置
 */
!function(){
    var orderNo;
    
    $(document).on('click', '.reset-account-link', function(){
        orderNo = $(this).data("orderno")
		pushHistory('page');
        mainView.router.load({pageName: 'resetUserPage'});
    });

    $(document).on('click', '.resell-account-link', function(){
        orderNo = $(this).data("orderno");
        myApp.showIndicator();

        $.ajax({
            type: 'GET',
            url: '/userCenter/getResellAccountByOrder',
            dataType: 'json',
            data: {
                format: 'json',
                "companyId": config.manageUid,
                platform: platform,
                authToken: authToken,
                deviceId: config.deviceId,
                productCode: config.productCode,
                orderNo: orderNo
            },
            success: function(data) {
                if(data.status != true){
                    layer.open({content: data.message,skin: 'msg',time: 4,anim:false });
                    return false;
                }
                var r;
                try{
                    r = data.data
                }catch(e){
                    layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
                    return;
                }
				pushHistory('page');
                mainView.router.load({pageName: 'modifygoods', query: r});
            },
            complete: function() {
                myApp.hideIndicator();
            },
            error: function() {
                layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
            }
        });
    });

    $(document).on('pageAfterAnimation', '.page[data-page=resetUserPage]', function (e) {
        $('#reset-user-span').text('获取中...');
        myApp.showIndicator();

        $.ajax({
            type: 'GET',
            url: '/userCenter/getAccountByOrder',
            dataType: 'json',
            data: {
                format: 'json',
                "companyId": config.manageUid,
                platform: platform,
                authToken: authToken,
                deviceId: config.deviceId,
                productCode: config.productCode,
                orderNo: orderNo
            },
            success: function(data) {
                if(data.status != true){
                    layer.open({content: data.message,skin: 'msg',time: 4,anim:false });
                    mainView.router.back();
                    return false;
                }
                var r;
                try{
                    r = data.data
                }catch(e){
                    layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
                    mainView.router.back();
                    return;
                }

                $('#reset-user-span').text(r.username)
            },
            complete: function() {
                myApp.hideIndicator();
            },
            error: function() {
                layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
                mainView.router.back();
            }
        });
    });

    $(document).on('click', '.submit.reset-btn', function(){
        var passwd = $('#pwd').val().trim(), repwd = $('#repwd').val().trim();
        if(passwd.length < 6 || passwd.length > 16){
            layer.open({content: '密码长度为6-20个字符',skin: 'msg',time: 4,anim:false });
            return false;
        }

        var n = /\d/.test(passwd), s = /[a-z]|[A-Z]/.test(passwd), t = /\W/.test(passwd);
        if( (n + s + t) < 2 ){
            layer.open({content: '密码强度过低，请设置数字、字母、符号至少两种',skin: 'msg',time: 4,anim:false });
            return false;
        }

        if(passwd !== repwd){
            layer.open({content: '两次密码输入不一致',skin: 'msg',time: 4,anim:false });
            return false;
        }

        myApp.showIndicator();
        $.ajax({
            type: 'POST',
            url: '/userCenter/resetAccount',
            dataType: 'json',
            data: {
                format: 'json',
                "companyId": config.manageUid,
                platform: platform,
                authToken: authToken,
                deviceId: config.deviceId,
                productCode: config.productCode,
                orderNo: orderNo,
                passwd: passwd,
                repwd: repwd
            },
            success: function(data) {
                if(data.status != true){
                    layer.open({content: data.message,skin: 'msg',time: 4,anim:false });
                    return false;
                }

                $(`#my-order-item-${orderNo}`).css('padding-bottom', '0').find('.orderInfoActionBar').remove();
                layer.open({content: '账号已重置成功！',skin: 'msg',time: 4,anim:false});
                setTimeout(function(){
                    mainView.router.back();
                },4000)
            },
            complete: function() {
                myApp.hideIndicator();
            },
            error: function() {
                layer.open({content: '系统异常，请再试一次吧',skin: 'msg',time: 4,anim:false });
            }
        });
    });
}()



function doActionFunction(action,params){
  var sendObject = new Object;
  sendObject.action = action;
  sendObject.params = params;
  
  var ua = navigator.userAgent;
  console.log('CallPlatformFunction:' + JSON.stringify(sendObject));
  //SDK个人中心和游戏盒子
  if(ua.indexOf('hermesgameAndroid') != -1 || ua.indexOf('QuickBrowserAndroid') != -1 || ua.indexOf("hermesgameiOS") != -1){
    if(typeof(JObject) == 'object'){
      return JObject.callAndroidFunction(JSON.stringify(sendObject));
    }
    // IOS
    if(ua.indexOf("hermesgameiOS") != -1 && typeof(window.parent.JObject) == 'object'){
        // var JObject = window.parent.JObject
        window.JObject = window.parent.JObject
        return JObject.callAndroidFunction(JSON.stringify(sendObject));
    }
  }
  return false;
}

var myPhotoBrowserStandalone = null;
var photoOpen = 0;
function photeNavTemp(dropBtn){
    var btn = '';
    if(true == dropBtn){
        btn = '<div class="right"><a class="deletePhoto" href="javascript:void(0)"></a></div>';
    }
    return '<div class="navbar">'
        +'<div class="navbar-inner">'
            +'<div class="left sliding">'
                +'<a href="#" class="link close-popup photo-browser-close-link {{#unless backLinkText}}icon-only{{/unless}} {{js "this.type === \'page\' ? \'back\' : \'\'"}}">'
                    +'<i class="icon icon-back {{iconsColorClass}}"></i>'
                    +'{{#if backLinkText}}<span>{{backLinkText}}</span>{{/if}}'
                +'</a>'
            +'</div>'
            +'<div class="center sliding">'
                +'<span class="photo-browser-current"></span> '
                +'<span class="photo-browser-of">{{ofText}}</span> '
                +'<span class="photo-browser-total"></span>'
            +'</div>'
            +btn
        +'</div>'
    +'</div>';
}

$(document).on('taphold','.thisAddImage', function () {
	$(".deleteLayer").css('visibility','visible').css('display','block').css('opacity',1);
	var $thisImage = $(this);
	delImage = myApp.modal({
		title:  '提示',
		text: '是否删除这张图片',
		
		buttons: [{
			text: '取消',
			onClick:function(){
                myApp.closeModal(delImage);
				$(".deleteLayer").css('visibility','hidden').css('display','none').css('opacity',0);
            }
		},{
			text: '<span style="color:red;">删除</span>',
			bold: true,
			close: false,
			onClick: function() {
				
				$.each(imgArray,function(k,v){
					if(v == $thisImage.attr('imageData')){
						imgArray.splice(k,1);
					}
				})
				$thisImage.remove();
                if(sellAction == 'add'){
                    chkSubmitForm();
                }
                if(sellAction == 'edt'){
                    chkEditForm();
                }
				myApp.closeModal(delImage);
				$(".deleteLayer").css('visibility','hidden').css('display','none').css('opacity',0);
			}
		}]
	});									
});

var delImage = null;
$(document).on('click','.deletePhoto',function(){
											   
	delImage = myApp.modal({
		title:  '提示',
		text: '是否删除这张图片',
		
		buttons: [{
			text: '取消',
			onClick:function(){
				
			}
		},{
			text: '<span style="color:red;">删除</span>',
			bold: true,
			close: false,
			onClick: function() {
				if(myPhotoBrowserStandalone == null){
					return;
				}
				var index  = myPhotoBrowserStandalone.activeIndex,
                    $img = $(".thisAddImage").eq(index),
                    arrIdx = imgArray.indexOf($img.attr('src'));

                    imgArray.splice(arrIdx, 1);
                    $img.remove();

				// var runIndex = 0;
				/*$.each($(".thisAddImage"),function(k,v){

					if(runIndex == index){
						
						$.each(imgArray,function(k1,v1){
							if(v1 == $(v).attr('imageData')){
								imgArray.splice(k1,1);
							}
						})
						
                        console.log(runIndex, index)
						$(v).remove();
						return true;
					}
					runIndex++;
				});*/
                if(sellAction == 'add'){
                    chkSubmitForm();
                }
                if(sellAction == 'edt'){
                    chkEditForm();
                }
				myApp.closeModal(delImage);
				myPhotoBrowserStandalone.close();
			}
		}]
	});									   
})

$(".scrollBox").scroll(function(){
	scrollOffset = $(this).scrollTop();
	console.log("scrollLog:"+scrollOffset);
})

var windowHeight = parseInt($(window).height());
var oldHeight = 640;
var newHeight = parseInt(oldHeight) + windowHeight;
var scrollOffset = 0;
var hasFocus = 0;
$('input').on('focus', function () {
	hasFocus = 1;

	var nowOffest = $(this).offset().top - 80;

	$(".scrollBody").css('height',newHeight+'px');
	
	
	console.log("thisElementOffset:"+nowOffest+"|scrollPos:"+scrollOffset);

	if(nowOffest > 0){
		for(var i = 0 ; i < nowOffest ; i++){
			$(".scrollBox").scrollTop(scrollOffset+i,200);
		}
	}else{
		for(var i = 0 ; i > nowOffest ; i--){
			$(".scrollBox").scrollTop(scrollOffset+i,200);
		}
	}
 
}).on('blur', function () {
	
	//判断所有input都没有被聚焦
	hasFocus = 0;
	
	setTimeout(function(){
		
		if(hasFocus == 0){
			
			setTimeout(function(){
				$(".scrollBox").scrollTo(0,0,200);
				setTimeout(function(){
					$(".scrollBody").css('height','auto');
				},200)
			},20);
		}
		
	},200);
})

function checkScreenType(){
	var height = $(window).height();
	var width = $(window).width();
	if(width > height){
		resetScreenOrientation(0);
	}
}

/*setInterval(function(){
	checkScreenType();
},1000);
*/
function resetScreenOrientation(type){
	doActionFunction('resetScreenOrientation',{screenOrientation:type,windowRate:1});
}

$(document).on('click','.thisAddImage',function(){
	
	var list = new Array();
	var initIndex = 0;
	var clickIndex = $(this).attr('num');
	var runIndex = 0;
    var imgData = '';
	$.each($(".thisAddImage"),function(k,v){
        imgData = $(v).attr('imageData');
        if(imgData.indexOf('http') == -1){
            imgData = `data:image/png;base64,${imgData}`;
        }
		list.push(imgData);
		if( clickIndex == $(v).attr('num')  ){
			initIndex = runIndex;
		}
		runIndex++;
	})
	
	myPhotoBrowserStandalone = myApp.photoBrowser({
		photos : list,
		backLinkText: '关闭',
		theme: 'dark',
		toolbar:false,
		ofText:'/',
		navbarTemplate:photeNavTemp(true),
		initialSlide:initIndex,
		onOpen:function(){
			photoOpen = 1;
		},
		onClose:function(){
			photoOpen = 0;
		},
		onSlideChangeStart:function(){
			if(delImage != null){
				myApp.closeModal(delImage);
			}
		},
	});
	if(myPhotoBrowserStandalone != null){
		pushHistory('page');
		myPhotoBrowserStandalone.open();
	}
})

function completePaypassword(params){
	if(params.payPass){
		config.userPayPass=params.payPass;
		if(params.rePay ==1){
			payByEwallet(params);
		}else{
			$(".submit.pay-btn").trigger("click");
		}
	}
}

$(window).on('popstate',function(e){
	var nowPage = mainView.url;
	if(myPhotoBrowserStandalone != null && photoOpen == 1){
		myPhotoBrowserStandalone.close();
		return;
	}else if(goodsBrowser != null && photoOpen == 1){
		goodsBrowser.close();
		return;
	}else if($("#siteFrameLayer").css('display') == 'block'){
		websiteClose();
		return;
	}else if(nowPage == '#index'){
		window.parent.postMessage({"action":"closeFrame"},"*");
		return;
	}else{
		mainView.router.back();
		return;
	}
})

var selectImageInt = 0,
    sellAction = 'add';
window.addEventListener('message', function (event) {

    if(!event.data.hasOwnProperty('action')){
        return;
    }
    
    if(event.data.action == 'closeFrame'){
        clearInterval(checkSiteOpen);
        checkSiteOpen = null;
        myApp.hideIndicator();
        $(".preloader-indicator-modal").css('margin-left','0px');
        websiteClose();
    }
    
    if(event.data.action == 'hideLoading'){
        myApp.hideIndicator();
        $(".preloader-indicator-modal").css('margin-left','0px');
    }
	
	if(event.data.action == "selectImage"){
		var imageList = event.data.params.path;
		var html = '';
		var saveImage = new Array();
		
		$.each(imageList,function(k,v){
			html += '<li class="img-items"><img num="'+selectImageInt+'" imageData="'+v+'" class="thisAddImage" src="data:image/png;base64,'+v+'"></li>';
            imgArray.push(v);
			selectImageInt = selectImageInt+1;
		});

        if(sellAction == 'add'){
    		chkSubmitForm();
    		// $("#showAccountImage").prepend(html);
            $(html).insertBefore('#add-img-btn');
        }
        if(sellAction == 'edt'){
            chkEditForm();
            // $("#showAccountImage2").prepend(html);
            $(html).insertBefore('#edt-img-btn');
        }

		myApp.hideIndicator();
	}
	
	if(event.data.action == 'completePayPassword'){
		completePaypassword(event.data.params);
	}
	
	if(event.data.action == "onBackButtonClick"){
		var nowPage = mainView.url;
		if(myPhotoBrowserStandalone != null && photoOpen == 1){
			myPhotoBrowserStandalone.close();
			return;
		}else if(goodsBrowser != null && photoOpen == 1){
			goodsBrowser.close();
			return;
		}else if($("#siteFrameLayer").css('display') == 'block'){
			websiteClose();
			return;
		}else if(nowPage == '#index'){
			window.parent.postMessage({"action":"closeFrame"},"*");
			return;
		}else{
			mainView.router.back();
			return;
		}
	}
	
	if(event.data.action == 'onDownProcessChange'){
		var packageName = event.data.packageName;
		var rate = event.data.rate;
		onDownProcessChange(packageName,rate);
	}
	
	if(event.data.action == "onDownLoadSuccess"){
		var packageName = event.data.packageName;
		var url = event.data.url;
		onDownLoadSuccess(packageName,url);
	}
	if(event.data.action == 'closePayPassWindow'){
		layer.open({content: '支付取消',skin: 'msg',time: 3,anim:false });
	}
	

}, false);
function websiteClose(){
    $('#siteFrame').attr('src', '');
    myApp.closeModal("#siteFrameLayer");
}
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

$(document).on('pageBeforeAnimation', '.page', function (e) {
	
	var pageName = $(this).attr('data-page');
	
  	window.parent.postMessage({"action":"pageChange","type":"pageIn","pageName":pageName},"*");
});




function onDownProcessChange(packageName,rate){
	
	var params = new Object();
	params.action = "onDownProcessChange";
	params.packageName = packageName;
	params.rate = rate;
	
	var iFrame = document.getElementById('siteFrame');
	if(iFrame){
		iFrame.contentWindow.postMessage(params, '*');
	}
	
}

function onDownLoadSuccess(packageName,url){
	
	var params = new Object();
	params.action = "onDownLoadSuccess";
	params.packageName = packageName;
	params.url = url;
	
	var iFrame = document.getElementById('siteFrame');
	if(iFrame){
		iFrame.contentWindow.postMessage(params, '*');
	}
}

function pushHistory(title) {

  if(config.theme == 'weixin'){
	  var state = {
		title: title,
		url: "#"
	  };
	  window.history.pushState(state, "title", "#");
  }
}

var mousePos = {x:0,y:0};
var mouseNow = {x:0,y:0};
// $(".pull-referer").touchstart(function(e){
$(document).on('touchstart', '.pull-referer', function(e){

	if(config.theme == 'weixin'){
		return;
	}

	var scrollRate = parseInt($(this).scrollTop());
	if(scrollRate != 0){
		mousePos = {x:0,y:0};
		return;
	}
	mousePos.x = e.touches[0].pageX;
	mousePos.y = e.touches[0].pageY;
	
	var maxCanPullLength = '200';
	var reachLength = '150';
	var set_maxCanPullLength = $(this).attr('maxCanPullLength');
	var set_reachLength = $(this).attr('reachLength')
	
	if(set_maxCanPullLength != null){
		maxCanPullLength = set_maxCanPullLength;
	}
	if(set_reachLength != null){
		reachLength = set_reachLength;
	}

	
	if($(this).attr('loadClass') != null){
		var className = $(this).attr('loadIconClass');
		var callbackClass = $(this).attr('callbackClass');
		$("." + className).addClass('isPulling').attr('reachLength',reachLength).attr('maxCanPullLength',maxCanPullLength).attr('callbackClass',callbackClass);
	}else{
		$(this).find('.loadStatsArea').addClass('isPulling').attr('reachLength',reachLength).attr('maxCanPullLength',maxCanPullLength);
	}
})

// $(".pull-referer").touchmove(function(e){
$(document).on('touchmove', '.pull-referer', function(e){

	if(mousePos.y == 0){
		return;
	}
	
	var $isPull = $(".isPulling");
	if($isPull.length <= 0){
		return;
	}
									  

	mouseNow.x = e.touches[0].pageX;
	mouseNow.y = e.touches[0].pageY;
	var pos = mouseNow.y - mousePos.y;
	
	var maxCanPullLength = parseInt($isPull.attr('maxCanPullLength'));
	
	if(pos <= -60){
		pos = -60;
	}
	if(pos >= maxCanPullLength){
		pos = maxCanPullLength;
	}
	
	$(".isPulling").css('transition-duration','0ms').css('transform','translate3d(0,'+pos+'px,0)');
	$(".isPulling").find('.load1Area').css('transform','rotate(30deg)');
})

function donePullReferer(){
	$(".isPulling").css('transition-duration','400ms').css('transform','translate3d(0,0px,0)');
	$(".isPulling").removeAttr('callback').removeAttr('maxCanPullLength').removeAttr('reachLength');
	$(".isPulling").removeClass('isPulling');
	mouseNow = {x:0,y:0};
	$(".topLayer").remove();
	$(".sendEvent").removeClass('sendEvent');
}

// $(".pull-referer").touchend(function(e){
$(document).on('touchend', '.pull-referer', function(e){
 	

	
	//判断拖动大小
	var moveLength = mouseNow.y - mousePos.y;
	
	//判断是否达到拖拉条件
	if(mousePos.x == 0 && mousePos.y == 0){
		donePullReferer();
		return;
	}
	var $isPull = $(".isPulling");
	if($isPull.length == 0){
		donePullReferer();
		return;
	}
	
	//达到的条件
	if(parseInt(moveLength) < 200){
		donePullReferer();
		return;
	}
	
	
	//达到下拉条件
	var reachLength = parseInt($isPull.attr('reachLength'));
	$(".isPulling").css('transition-duration','400ms').css('transform','translate3d(0,'+reachLength+'px,0)');
	
	if(!$(".isPulling").hasClass('sendEvent')){
		$(".isPulling").addClass('sendEvent');
		var callback = $(".isPulling").attr('callback');
		if($(".topLayer").length != 0){
			$(".topLayer").remove();
		}
		$("body").append('<div class="topLayer"></div>');
		if(callback != null){
			$("." + callback).trigger('doPull');
		}else{
			$(".isPulling").trigger('doPull');
		}
	}
})


$(document).on('doPull','.goodsPull',function(){
	$("#goodsList").attr('nowPage', 0);
	setTimeout(function(){
		loadGoodsList();
	},500);
});
//微信支付js
function checkWxJs(payBackData){
	if (typeof WeixinJSBridge == "undefined"){
	   if( document.addEventListener ){
	       document.addEventListener('WeixinJSBridgeReady', wxJsPayOrig(payBackData), false);
	   }else if (document.attachEvent){
	       document.attachEvent('WeixinJSBridgeReady', wxJsPayOrig(payBackData)); 
	       document.attachEvent('onWeixinJSBridgeReady', wxJsPayOrig(payBackData));
	   }
	}else{
		wxJsPayOrig(payBackData);
	}
}
function wxJsPayOrig(payBackData){
	WeixinJSBridge.invoke(
		'getBrandWCPayRequest',{
			"appId":payBackData.appId,
			"timeStamp":payBackData.timeStamp,
			"nonceStr":payBackData.nonceStr,
			"package":payBackData.package,
			"signType":payBackData.signType,
			"paySign":payBackData.paySign
		},
		function(res){
			if(res.err_msg=="get_brand_wcpay_request:ok"){
				$("#order-msg").html("支付成功");
			}else if(res.err_msg=="get_brand_wcpay_request:cancel"){
				$("#order-msg").html("支付取消");
				if(config.theme == 'weixin'){
					history.go(-1);
				}
			}else{
				$("#order-msg").html("支付失败");
				if(config.theme == 'weixin'){
					history.go(-1);
				}
			}
		}
	);
}