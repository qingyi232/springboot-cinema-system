import {createRouter, createWebHistory} from 'vue-router'
import FrontLayout from "@/views/layout/FrontLayout.vue";
import AdminLayout from "@/views/layout/AdminLayout.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: getRoutes()
})

function getRoutes() {
    let defaultRoutes = [
        {
            path: '/',
            name: 'front',
            component: FrontLayout,
            redirect: "/index",
            children: [{
                path: "index",
                name: "index",
                component: () =>
                    import ('../views/front/Index.vue')
            }, {
                path: "editCurrentUser",
                name: "editCurrentUser",
                component: () =>
                    import ('../views/EditCurrentUser.vue')
            }, {
                path: "editPassword",
                name: "front-editPassword",
                component: () =>
                    import ('../views/EditCurrentUser.vue')
            }, {
                path: "balanceInfo",
                name: "front-balanceInfo",
                component: () =>
                    import ('../views/BalanceInfo.vue')
            }, {
                path: 'movieList',
                name: 'front-movieList',
                component: () => import('../views/front/MovieList.vue')
            },

                {
                    path: 'cinemaList',
                    name: 'front-cinemaList',
                    component: () => import('../views/front/CinemaList.vue')
                },
                {
                    path: 'cinemaDetails/:id',
                    name: 'front-cinemaDetails',
                    component: () => import('../views/front/CinemaDetails.vue')
                },
                {
                    path: 'movieDetails/:id',
                    name: 'front-movieDetails',
                    component: () => import('../views/front/MovieDetails.vue')
                },
                {
                    path: 'movieOrder',
                    name: 'front-MovieOrder',
                    component: () => import('../views/front/MovieOrder.vue')
                },
                {
                    path: 'goodsList',
                    name: 'front-goodsList',
                    component: () => import('../views/front/GoodsList.vue')
                },
                {
                    path: 'personalCenter',
                    name: 'front-personalCenter',
                    component: () => import('../views/front/PersonalCenter.vue')
                },
            ]
        },
        {
            path: '/admin',
            name: 'admin',
            component: AdminLayout,
            redirect: "/admin/home",
            children: [{
                path: "home",
                name: "admin-home",
                component: () =>
                    import ('../views/admin/Home.vue')
            },
                {
                    path: 'editCurrentUser',
                    name: 'admin-editCurrentUser',
                    component: () =>
                        import ('../views/EditCurrentUser.vue')
                },
                {
                    path: 'editPassword',
                    name: 'admin-editPassword',
                    component: () =>
                        import ('../views/EditCurrentUser.vue')
                },
                {
                    path: 'admin',
                    name: 'Admin',
                    component: () =>
                        import ('../views/admin/AdminManage.vue')
                },
                {
                    path: 'user',
                    name: 'admin-user',
                    component: () =>
                        import ('../views/admin/UserManage.vue')
                }
                ,
                {
                    path: 'movieType',
                    name: 'admin-movieType',
                    component: () =>
                        import ('../views/admin/MovieTypeManage.vue')
                }
                ,
                {
                    path: 'movieRegion',
                    name: 'admin-movieRegion',
                    component: () =>
                        import ('../views/admin/MovieRegionManage.vue')
                }
                ,
                {
                    path: 'movie',
                    name: 'admin-movie',
                    component: () =>
                        import ('../views/admin/MovieManage.vue')
                }
                ,
                {
                    path: 'goods',
                    name: 'admin-goods',
                    component: () =>
                        import ('../views/admin/GoodsManage.vue')
                }

                ,
                {
                    path: 'movieCollect',
                    name: 'admin-movieCollect',
                    component: () =>
                        import ('../views/admin/MovieCollectManage.vue')
                }
                ,
                {
                    path: 'movieBrowsingHistory',
                    name: 'admin-movieBrowsingHistory',
                    component: () =>
                        import ('../views/admin/MovieBrowsingHistoryManage.vue')
                }
                ,
                {
                    path: 'movieOrder',
                    name: 'admin-movieOrder',
                    component: () =>
                        import ('../views/admin/MovieOrderManage.vue')
                }
                ,
                {
                    path: 'movieOrderEvaluate',
                    name: 'admin-movieOrderEvaluate',
                    component: () =>
                        import ('../views/admin/MovieOrderEvaluateManage.vue')
                }
                ,
                {
                    path: 'goodsOrder',
                    name: 'admin-goodsOrder',
                    component: () =>
                        import ('../views/admin/GoodsOrderManage.vue')
                }
                ,
                {
                    path: 'ticketVerify',
                    name: 'admin-ticketVerify',
                    component: () =>
                        import ('../views/admin/TicketVerifyManage.vue')
                }
                ,
                {
                    path: 'slideshow',
                    name: 'admin-slideshow',
                    component: () =>
                        import ('../views/admin/SlideshowManage.vue')
                }
                ,
                {
                    path: 'cinema',
                    name: 'admin-cinema',
                    component: () =>
                        import ('../views/admin/CinemaManage.vue')
                }
                ,
                {
                    path: 'movieRoom',
                    name: 'admin-movieRoom',
                    component: () =>
                        import ('../views/admin/MovieRoomManage.vue')
                }
                ,
                {
                    path: 'screeningPlan',
                    name: 'admin-screeningPlan',
                    component: () =>
                        import ('../views/admin/ScreeningPlanManage.vue')
                }


            ]
        },
        {
            path: "/login",
            name: "login",
            component: () =>
                import ('../views/Login.vue')
        }, {
            path: "/register",
            name: "register",
            component: () =>
                import ('../views/Register.vue')
        }, {
            path: "/retrievePassword",
            name: "front-retrievePassword",
            component: () =>
                import ('../views/RetrievePassword.vue')
        }];
    defaultRoutes.push({
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        meta: {
            name: ''
        },
        component: () => import ('../views/404.vue')
    })
    return defaultRoutes;
}

router.beforeEach((to, from, next) => {
    next();
});
export default router
