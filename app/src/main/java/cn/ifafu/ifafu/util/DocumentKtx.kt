package cn.ifafu.ifafu.util

import org.jsoup.nodes.Element
import org.jsoup.select.Elements


val Element.byId: Getter<String, Element>
    get() = Getter {
        return@Getter this.getElementById(it)
    }

val Element.byAttr: Getter<String, Elements>
    get() = Getter {
        return@Getter this.getElementsByAttribute(it)
    }

val Element.byClass: Getter<String, Elements>
    get() = Getter {
        return@Getter this.getElementsByClass(it)
    }

val Element.byTag: Getter<String, Elements>
    get() = Getter {
        return@Getter this.getElementsByTag(it)
    }

val Element.byAttrStart: Getter<String, Elements>
    get() = Getter {
        return@Getter this.getElementsByAttributeStarting(it)
    }

class Getter<V, R>(private val callback: (V) -> R?) {

    operator fun get(v: V): R? {
        return callback(v)
    }
}
