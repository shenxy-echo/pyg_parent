 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//根据ID查询实体
	$scope.findOne=function(){
        var id= $location.search()['id'];//获取从上一个页面传进来的id参数值
        if(id==null){
            return ;
        }
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
                editor.html($scope.entity.tbGoodsDesc.introduction);//向富文本编辑器添加商品介绍
                $scope.entity.tbGoodsDesc.itemImages = JSON.parse($scope.entity.tbGoodsDesc.itemImages);//将字符串格式的图片信息转换为json对象
                $scope.entity.tbGoodsDesc.specificationItems =JSON.parse($scope.entity.tbGoodsDesc.specificationItems);//将字符串形式的规格信息转换为json对象
                for( var i=0;i<$scope.entity.itemList.length;i++ ){
                    $scope.entity.itemList[i].spec = JSON.parse( $scope.entity.itemList[i].spec);
                }
            }
		);				
	}

    //根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue = function(specName,optionName){
        var items = $scope.entity.tbGoodsDesc.specificationItems;
        var object =  $scope.searchObjectByKey(items,"attributeName",specName);
        if(object == null){
            return false;
        }else{
            if(object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else{
                return false;
            }
        }
    }
//封装后的goods实体
    $scope.entity={tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]},itemList:[]}
    $scope.selectModel = "";
	//保存 
	$scope.save=function(){
        $scope.entity.tbGoodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加
		}
		serviceObject.success(
			function(response){
                alert(response.message);
				if(response.success){
					//重新查询 
		        	location.href="goods.html";
				}
			}		
		);				
	}

	//上传文件
	$scope.upload = function(){
        uploadService.uploadFile().success(
            function (response) {
                if(response.success){
                    $scope.image_entity.url = response.message;
                }else{
                    alert(response.message)
                }
            }
        ).error(
            function () {
                alert("上传发生错误");
            }
        );
    }
//上传图片
    $scope.add_image_entity = function(){
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
	}
//删除图片
	$scope.remove_entity_image = function(index){
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1);
	}
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

//读取一级分类
    $scope.selectItemCat1List=function(){
        itemCatService.findByPid(0).success(
            function(response){
                $scope.itemCat1List=response;
            }
        );
    }

//读取二级分类
    $scope.$watch('entity.tbGoods.category1Id', function(newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findByPid(newValue).success(
            function(response){
                $scope.itemCat2List=response;
            }
        );
    });


//读取三级分类
    $scope.$watch('entity.tbGoods.category2Id', function(newValue, oldValue) {
        //根据选择的值，查询二级分类
        itemCatService.findByPid(newValue).success(
            function(response){
                $scope.itemCat3List=response;
            }
        );
    });


//三级分类选择后  读取模板ID
    $scope.$watch('entity.tbGoods.category3Id', function(newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function(response){
                $scope.entity.tbGoods.typeTemplateId=response.typeId; //更新模板ID
            }
        );
    });
//模板ID选择后  更新品牌列表
    $scope.$watch('entity.tbGoods.typeTemplateId', function(newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function(response){
                $scope.typeTemplate=response;//获取类型模板
                $scope.typeTemplate.brandIds= JSON.parse( $scope.typeTemplate.brandIds);//品牌列表
            }
        );

        //查询规格列表
        typeTemplateService.findSpecList(newValue).success(
            function(response){
                $scope.specList=response;
            }
        );
    });

    //[{“attributeName”:”规格名称”,”attributeValue”:[“规格选项1”,“规格选项2”.... ]  } , ....  ]
    $scope.updateSpecAttribute = function ($event,name,value) {
        var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,"attributeName",name);
        if(object != null){
            if($event.target.checked){
                object.attributeValue.push(value);
            }else{
              object.attributeValue.splice(object.attributeValue.indexOf(value),1);
              if(object.attributeValue.length == 0 ){
                  $scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(object),1);
              }
            }
        }else{
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
        }
    }

    $scope.searchObjectByKey = function (list,key,name) {
        for (var i = 0; i<list.length;i++){
            if(list[i][key]== name ){
                return list[i];
            }
        }
        return null;
    }

    $scope.createItemList = function () {
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始
        var specItems = $scope.entity.tbGoodsDesc.specificationItems;
        for (var i = 0; i < specItems.length; i++) {
            $scope.entity.itemList = $scope.addColumn($scope.entity.itemList,specItems[i].attributeName,specItems[i].attributeValue);
        }
    }
    
    $scope.addColumn = function (list,columnName,columnValues) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态

    $scope.itemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        );
    }

    $scope.updateStatus = function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectId = [];
                }else {
                    alert(response.message);
                }
            }
        );
    }
});	
