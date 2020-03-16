package cn.ifafu.ifafu.view.custom

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.ui.main.oldTheme.bean.Menu
import cn.ifafu.ifafu.util.DensityUtils
import cn.ifafu.ifafu.view.listener.TabClickListener
import java.util.*

class LeftMenu(private val content: LinearLayout) : View.OnClickListener {

    private val context = content.context
    
    private val tabMap: MutableMap<Int, String> = HashMap()

    private var tabClickListener: TabClickListener? = null

    override fun onClick(v: View?) {
        tabMap[v?.id]?.run {
            tabClickListener?.onTabClick(this)
        }
    }

    fun make(menu: cn.ifafu.ifafu.ui.main.oldTheme.bean.Menu): LeftMenu {
        for ((key, value) in menu.map.entries) {
            val u = Unit(key, content)
            for (v in value) {
                u.addTab(v)
            }
            u.draw()
        }
        return this
    }

    fun setTabClickListener(listener: TabClickListener): LeftMenu {
        tabClickListener = listener
        return this
    }

    inner class Unit(private val unitName: String, private val rootView: LinearLayout) {

        //First:Name  Second:IconRes
        private val tabList: MutableList<Menu.Item> = ArrayList()

        fun addTab(pair: Menu.Item) {
            tabList.add(pair)
        }

        fun draw() {
            //  Add unit split Line
            val splitLine = ImageView(content.context)
            splitLine.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            splitLine.setBackgroundResource(R.drawable.shape_line_split)
            rootView.addView(splitLine)

            //  Add unit name
            val unitNameView = TextView(context)
            unitNameView.text = unitName
            unitNameView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    DensityUtils.dp2px(context, 26f)).apply {
                marginStart = DensityUtils.dp2px(context, 12f)
            }
            unitNameView.textSize = 12f
            unitNameView.setTextColor(Color.parseColor("#ffffff"))
            unitNameView.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            rootView.addView(unitNameView)

            //  New tabList list
            val tabsView = LinearLayout(context)
            tabsView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            tabsView.orientation = LinearLayout.HORIZONTAL

            val fillView = LinearLayout(context)
            fillView.layoutParams = LinearLayout.LayoutParams(
                    DensityUtils.dp2px(context, 20f),
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f)
            tabsView.addView(fillView)

            val tabsListView = LinearLayout(context)
            tabsListView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    12f)
            tabsListView.orientation = LinearLayout.VERTICAL
            tabsView.addView(tabsListView)

            rootView.addView(tabsView)

            var first = true
            for (tab in tabList) {
                if (first) {
                    first = false
                } else {
                    rootView.addView(drawSplitLine())
                }
                val param = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    marginStart = DensityUtils.dp2px(context, 20F)
                }
                rootView.addView(drawTab(tab), param)
            }
        }

        private fun drawTab(pair: Menu.Item): View {
            //绘制Tab根布局
            val tabView = LinearLayout(context)
            tabView.orientation = LinearLayout.HORIZONTAL
            tabView.gravity = Gravity.CENTER_VERTICAL
            //绘制图标
            val iconView = ImageView(context)
            iconView.layoutParams = LinearLayout.LayoutParams(
                    DensityUtils.dp2px(context, 30F),
                    DensityUtils.dp2px(context, 30F))
            iconView.setImageResource(pair.icon)
            tabView.addView(iconView)

            //标题文本
            val nameTv = TextView(context)
            val tabNameViewParams = LinearLayout.LayoutParams(
                    0,
                    DensityUtils.dp2px(context, 50F)).apply {
                marginStart = DensityUtils.dp2px(context, 10F)
                weight = 1F
            }
            nameTv.layoutParams = tabNameViewParams
            nameTv.text = pair.title
            nameTv.setTextColor(Color.WHITE)
            nameTv.textSize = 18F
            nameTv.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            tabView.addView(nameTv)

            //绘制尾部 > 图标
            val goView = ImageView(context)
            goView.layoutParams = LinearLayout.LayoutParams(
                    DensityUtils.dp2px(context, 50F),
                    DensityUtils.dp2px(context, 15F))
            goView.setImageResource(R.drawable.ic_right)
            tabView.addView(goView)

            val newId = View.generateViewId()
            tabView.id = newId
            tabMap[newId] = pair.title
            tabView.setOnClickListener(this@LeftMenu)

            return tabView
        }

        //绘制分割线
        private fun drawSplitLine(): View {
            val line = ImageView(context)
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    DensityUtils.dp2px(context, 0.5F)).apply {
                marginStart = DensityUtils.dp2px(context, 20F)
            }
            line.layoutParams = params
            line.setBackgroundColor(Color.WHITE)
            return line
        }

    }

}
