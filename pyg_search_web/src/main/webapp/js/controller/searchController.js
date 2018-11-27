app.controller('searchController',function($scope,$location,searchService){
    //搜索
    $scope.search=function(){
        $scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo)
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
                $scope.pageLabel();
            }
        );
    }
    //回车出发搜索方法
    $scope.keyEvent = function(e) {
        var keycode = window.event?e.keyCode:e.which;
        if(keycode==13){
            $scope.searchMap.pageNo=1;
            $scope.search();
        }
    }
    //面包屑的显示
    $scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20,'sortField':'','sort':'' };
    $scope.addSearchItem = function (key,value) {
        if(key == 'category' || key ==  'brand'||key == 'price'){
            $scope.searchMap[key] = value;
        }else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }

    //删除面包屑
    $scope.removeSearchItem = function (key) {
        if(key == 'category' || key ==  'brand'||key=='price'){
            $scope.searchMap[key] = "";
        }else {
            delete $scope.searchMap.spec[key] ;
        }
        $scope.search();
    }


    $scope.pageLabel = function () {
        $scope.pageList = [];
        var maxPage = $scope.resultMap.totalPages;
        var firstPage = 1;
        var endPage = maxPage;
        $scope.firstDot = true;
        $scope.endDot = true;
        if($scope.resultMap.totalPages>5){
           if($scope.searchMap.pageNo <=3 ){
                endPage = 5;
               $scope.firstDot =false;
           }else if($scope.searchMap.pageNo >= maxPage -2){
                firstPage = maxPage -4 ;
               $scope.endDot = false;
           }else{
               firstPage = $scope.searchMap.pageNo - 2;
               endPage = $scope.searchMap.pageNo + 2;
           }
        }else{
            $scope.firstDot = false;
            $scope.endDot = false;
        }
        for(var i = firstPage; i <= endPage;i++){
            $scope.pageList.push(i);
        }
    }
    
    $scope.queryByPageNo = function (pageNo) {
        if(pageNo <1 || pageNo > $scope.resultMap.totalPages ){
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    }

    //判断当前页为第一页
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }

//判断当前页是否未最后一页
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }
    
    $scope.searchSort = function (sortField,sort) {
            $scope.searchMap.sortField = sortField;
            $scope.searchMap.sort = sort;
            $scope.search();
    }

    $scope.keywordsIsBrand = function () {
        for(var i = 0; i < $scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    $scope.loadKeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
});