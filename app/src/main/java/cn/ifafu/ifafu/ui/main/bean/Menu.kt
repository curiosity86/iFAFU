package cn.ifafu.ifafu.ui.main.bean

class Menu(val map: Map<String, List<Item>>) {
    class Item(val title: String, val icon: Int)
}