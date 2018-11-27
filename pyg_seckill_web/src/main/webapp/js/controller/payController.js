 //控制层 
app.controller('payController' ,function($scope,$location,$controller ,payService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.createNative = function () {
		payService.createNative().success(
			function (response) {
                $scope.money=  (response.total_fee/100).toFixed(2) ;	//金额
                $scope.out_trade_no= response.out_trade_no;//订单号
                //二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
              queryPayStatus(response.out_trade_no);//查询支付状态
            }
		);
    }

    //查询支付状态
    queryPayStatus=function(out_trade_no){
        payService.queryPayStatus(out_trade_no).success(
            function(response){
                if(response.success){
                    location.href="paysuccess.html#?money="+$scope.money;
                }else{
                    if(response.message=='二维码超时'){
                        location.href="paytimeOut.jsp";
                    }else{
                        location.href="payfail.html";
                    }
                }
            }
        );
    }

    //获取前一个页面传过来的信息并展示在支付成功页面中
	$scope.getMoney = function () {
		return $location.search()['money'];
    }
});
