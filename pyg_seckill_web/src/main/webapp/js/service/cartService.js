//服务层
app.service('cartService',function($http){
    //获取购物车
    this.findCartList = function () {
        return $http.get("cart/findCartList.do");
    }

    this.addGoodsToCart = function (itemId,num) {
        return $http.get("cart/addGoodsToCart.do?itemId="+itemId+"&num="+num);
    }

    this.findAddressList = function () {
        return $http.get("address/findListByLoginUser.do");
    }

    this.submitOrder = function (order) {
        return $http.post("order/add.do",order);
    }


});
