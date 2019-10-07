package cn.ifafu.ifafu.mvp.electives

import cn.ifafu.ifafu.base.i.IView
import cn.ifafu.ifafu.base.ifafu.IZFModel
import cn.ifafu.ifafu.base.ifafu.IZFPresenter

class ElectiveContract {

    interface View: IView {

    }

    interface Presenter: IZFPresenter {

    }

    interface Model: IZFModel {

    }
}