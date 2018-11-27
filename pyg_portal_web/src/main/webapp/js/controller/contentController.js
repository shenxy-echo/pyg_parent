//控制层
app.controller('contentController' ,function($scope,$controller   ,contentService){
    $scope.contentList=[];
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
                console.info($scope.contentList[categoryId])
            }
        );
    }
    //回车出发搜索方法
    $scope.keyEvent = function(e) {
        var keycode = window.event?e.keyCode:e.which;
        if(keycode==13){
            //$scope.searchMap.pageNo=1;
            $scope.search();
        }
    }
    $scope.search = function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});
