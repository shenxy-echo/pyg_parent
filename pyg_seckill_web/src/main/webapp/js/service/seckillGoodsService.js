//服务层
app.service('seckillGoodsService',function($http){
    //获取购物车
    this.findList = function () {
        return $http.get("seckillGoods/findList.do");
    }

    this.findOne = function (id) {
        return $http.get("seckillGoods/findOneFromRedis.do?id="+id);
    }
    this.submitOrder = function (goodsId) {
        return $http.get("seckillOrder/submitOrder.do?goodsId="+goodsId);
    }

});
