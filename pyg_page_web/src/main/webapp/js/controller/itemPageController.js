app.controller('itemController',function($scope,$http){
    //数量操作
    $scope.addNum=function(x){
        $scope.num=$scope.num+x;
        if($scope.num<1){
            $scope.num=1;
        }
    }

    //初始化时，加载默认第一个sku
    $scope.loadSku = function(){
    	$scope.sku = skuList[0];
    	specificationList = JSON.parse(JSON.stringify(skuList[0].spec));
    }
    //存储选择的规格
    specificationList = {};

    //判断页面规格是否被选择
    $scope.isSelected = function(key,value){
    	if(specificationList[key] == value){
    		return true;
    	}
    	return false;
    }

    //选择规格后，将其进行存储，并对比skuList选择对应的sku，从而显示对应的标题和价格
    $scope.selectedSpec = function(key,value){
    	specificationList[key] =value;
    	searchSku();
    }

    //将存储的规格和SkuList中的sku依次对比，从而对$scope.sku重新复制
    searchSku = function(){
    	for (var i = 0; i < skuList.length; i++) {
    		if(matchObjec(specificationList,skuList[i].spec)){
    			$scope.sku = skuList[i];
    			return;
    		}
    	}
    	$scope.sku = {id:0,title:'------',price:0};//若无匹配项，则指定属性值
    }

    //对比两个json对象
    matchObjec = function(map1,map2){
    	for(var i in map1){
    		if(map1[i] != map2[i]){
    			return false;
    		}
    	}

    	for(var i in map2){
    		if(map2[i] != map1[i]){
    			return false;
    		}
    	}

    	return true;
    }

    //加入购物车
    $scope.addToCar=function(){
        $http.get('http://localhost:9107/cart/addGoodsToCart.do?itemId='
            + $scope.sku.id +'&num='+$scope.num,{'withCredentials':true}).success(
            function(response){
                if(response.success){
                    location.href='http://localhost:9107/cart.html';//跳转到购物车页面
                }else{
                    alert(response.message);
                }
            }
        );
    }
});