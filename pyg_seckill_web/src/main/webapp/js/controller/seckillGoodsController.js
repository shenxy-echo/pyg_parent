 //控制层 
app.controller('seckillGoodsController' ,function($scope,$location,$interval,$controller ,seckillGoodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.findList = function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    $scope.findOne = function () {
        var id = $location.search()['id'];
        seckillGoodsService.findOne(id).success(
            function (response) {
                $scope.entityOne = response;

                var second = Math.floor(( new Date($scope.entityOne.endTime).getTime() - new Date().getTime()) / 1000);
                $scope.title = "";
               var time =  $interval(
                   function () {
                        if(second>0){
                            second = second -1;
                            $scope.title = convertTimeString(second);
                        }else{
                            $interval.cancel(time);
                            alert("秒杀服务已结束");
                        }
                   },1000
               );
            }
        );
    }

    convertTimeString = function (second) {
        var days= Math.floor( second/(60*60*24));//天数
        var hours= Math.floor( (second-days*60*60*24)/(60*60) );//小时数
        var minutes= Math.floor(  (second -days*60*60*24 - hours*60*60)/60    );//分钟数
        var seconds= second -days*60*60*24 - hours*60*60 -minutes*60; //秒数
        var timeString="";
        if(days > 0){
            timeString = days + "天 ";
        }
        return timeString+hours+":"+minutes+":"+seconds;
    }

    $scope.submitOrder = function () {
        seckillGoodsService.submitOrder($scope.entityOne.id).success(
            function (response) {
                if(response.success){
                    alert("下单成功，请在一分钟之内完成支付");
                    location.href = "pay.html";
                }else{
                    alert(response.message);
                }
            }
        );
    }

});
