package tk.lorddarthart.accurateweathertestapp.application.model

class TranslationModel {
    var mCode: Int = 0
    var mLang: String = ""
    var mText: MutableList<String> = mutableListOf()

    constructor(mCode: Int, mLang: String, mText: MutableList<String>) {
        this.mCode = mCode
        this.mLang = mLang
        this.mText = mText
    }

    constructor(mText: MutableList<String>) {
        this.mText = mText
    }

    constructor()
}