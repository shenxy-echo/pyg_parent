//服务层
app.service('itemCatService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../itemCat/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../itemCat/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../itemCat/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../itemCat/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../itemCat/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../itemCat/deleteAll.do?ids='+ids);
	}
	//搜索
/*	this.search=function(page,rows,searchEntity){
		return $http.post('../itemCat/search.do?page='+page+"&rows="+rows, searchEntity);
	}*/
    this.search=function(page,rows,id){
        return $http.post('../itemCat/search.do?page='+page+"&rows="+rows+"&id="+id );
    }

	//根据父ID查找分类信息
    this.findByPid = function (id) {
		return $http.get("../itemCat/findByPid.do?id="+id);
    }
    

});
