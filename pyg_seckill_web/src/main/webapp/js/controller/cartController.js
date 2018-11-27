 //控制层 
app.controller('cartController' ,function($scope,$controller   ,cartService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.findCartList = function () {
		cartService.findCartList().success(
			function (response) {
				$scope.cartList = response;
				$scope.total = {totalNum:0,totalMoney:0};
				for(var i = 0; i < $scope.cartList.length;i++){
					var cart = $scope.cartList[i];
					for(var j = 0;j<cart.orderItemList.length;j++){
						$scope.total.totalNum += cart.orderItemList[j].num;
						$scope.total.totalMoney += cart.orderItemList[j].totalFee;
					}
				}
            }
		);
    }

    $scope.addGoodsToCart = function (itemId,num) {
		cartService.addGoodsToCart(itemId,num).success(
			function (response) {
				if(response.success){
					$scope.findCartList();
				}else{
					alert(response.message);
				}
            }
		);
    }

    $scope.findAddressList = function () {
		cartService.findAddressList().success(
			function (response) {
				$scope.addressList = response;
				for(var i = 0; i<$scope.addressList.length;i++){
					if($scope.addressList[i].isDefault == '1'){
						$scope.address = $scope.addressList[i];
						break;
					}
				}
            }
		);
    }

    $scope.isSelected = function (address) {
		if(address == $scope.address){
			return true;
		}else{
			return false;
		}
    }

    $scope.selectAddress = function (address) {
		$scope.address = address;
    }

    $scope.order = {paymentType:'1'}
    $scope.selectPayType=function (type) {
        $scope.order.paymentType = type;
    }

    $scope.submitOrder = function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
           function (response) {
               if(response.success){
                    if($scope.order.paymentType=='1'){
                        location.href="pay.html";
                    }else{
                        location.href="paysuccess.html";
                    }
               }else {
                   location.href="payfail.html";
               }
           }
        );
    }
});
