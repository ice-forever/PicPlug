package nju.eur3ka

enum class PicType(var ext: String, val header: String) {
    JPEG("jpg", "FFD8"),
    PNG("png", "89504E47"),
    GIF("gif", "47494638"),
    BMP("bmp", "424D"),
    WEBP("webp","52494646"),
    UNKNOWN("unknown", "00FF00FF")
}