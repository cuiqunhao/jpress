/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.module.product.controller;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.JsoupUtils;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.model.UserFavorite;
import io.jpress.module.product.model.ProductComment;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.module.product.service.ProductService;
import io.jpress.service.UserFavoriteService;
import io.jpress.web.base.UcenterControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.product.admin
 */
@RequestMapping(value = "/ucenter/product",viewPath = "/WEB-INF/views/ucenter/")
public class ProductUCenterController extends UcenterControllerBase {

    @Inject
    private ProductService articleService;

    @Inject
    private ProductCategoryService categoryService;

    @Inject
    private ProductCommentService commentService;

    @Inject
    private UserFavoriteService favoriteService;





    @UCenterMenu(text = "产品评论", groupId = "comment", order = 0)
    public void comment() {
        Page<ProductComment> page = commentService._paginateByUserId(getPagePara(), 10, getLoginedUser().getId());
        setAttr("page", page);
        render("product/comment_list.html");
    }


    @UCenterMenu(text = "产品收藏", groupId = "favorite", order = 0)
    public void favorite() {
        Page<UserFavorite> page = favoriteService.paginateByUserIdAndType(getPagePara(),10,getLoginedUser().getId(),"product");
        setAttr("page", page);
        render("product/product_favorite.html");
    }

    public void doDelFavorite(){
        UserFavorite userFavorite = favoriteService.findById(getPara("id"));

        if (isLoginedUserModel(userFavorite)){
            favoriteService.delete(userFavorite);
        }
        renderOkJson();
    }

    /**
     * 评论编辑 页面
     */
    public void commentEdit() {
        long id = getIdPara();
        ProductComment comment = commentService.findById(id);

//        if (commentService.isOwn(comment, getLoginedUser().getId()) == false) {
//            renderError(404);
//            return;
//        }

        setAttr("comment", comment);
        render("article/comment_edit.html");
    }

    public void doCommentSave() {
        ProductComment comment = getBean(ProductComment.class, "comment");
//        if (commentService.isOwn(comment, getLoginedUser().getId()) == false) {
//            renderJson(Ret.fail().set("message", "非法操作"));
//            return;
//        }

        //只保留的基本的html，其他的html比如<script>将会被清除
        String content = JsoupUtils.clean(comment.getContent());
        comment.setContent(content);

        comment.setUserId(getLoginedUser().getId());
        commentService.saveOrUpdate(comment);
        renderOkJson();
    }

    public void doCommentDel() {

        long id = getIdPara();

        ProductComment comment = commentService.findById(id);
        if (comment == null) {
            renderFailJson();
            return;
        }


//        if (commentService.isOwn(comment, getLoginedUser().getId()) == false) {
//            renderJson(Ret.fail().set("message", "非法操作"));
//            return;
//        }

        renderJson(commentService.deleteById(id) ? OK : FAIL);
    }

}