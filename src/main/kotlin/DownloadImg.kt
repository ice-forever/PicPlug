package nju.eur3ka

import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi
import nju.eur3ka.ext.toHexString
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.imageio.ImageIO
import javax.imageio.spi.IIORegistry


fun downloadImg(url: URL, imagePath: String, tryCount: Int): Pair<PicType, String> {
    return if (tryCount > 0) try {
        var fileName = "${System.currentTimeMillis()}"
        val path = Paths.get("$imagePath/$fileName")
        // 基于NIO来下载网络上的图片
        FileChannel.open(
            path,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE
        ).use {
            it.transferFrom(Channels.newChannel(url.openStream()), 0, Long.MAX_VALUE)
        }
        // 读取图片文件前4位字节 检测文件类型
        val byteBuffer = ByteBuffer.allocate(4)
        FileChannel.open(path, StandardOpenOption.READ).use { it.read(byteBuffer) }
        val header = byteBuffer.array().toHexString().uppercase()
        var type = when {
            header.startsWith(PicType.JPEG.header) -> PicType.JPEG
            header.startsWith(PicType.PNG.header) -> PicType.PNG
            header.startsWith(PicType.GIF.header) -> PicType.GIF
            header.startsWith(PicType.BMP.header) -> PicType.BMP
            header.startsWith(PicType.WEBP.header) -> PicType.WEBP
            else -> {
                PicPlug.logger.error("Unknown Image: HEAD=$header")
                PicType.UNKNOWN
            }
        }
        val pngFileName = fileName.plus(".").plus("png")
        fileName = fileName.plus(".").plus(type.ext)
        // 重命名图片文件
        path.toFile().renameTo(Paths.get("$imagePath/$fileName").toFile())
        if (type.ext == "webp") {
            PicPlug.logger.info("$imagePath\\$fileName")
            convertWebpToPng("$imagePath\\$fileName", "$imagePath\\${pngFileName}")
            type = PicType.PNG
            fileName = pngFileName
        }
        type to fileName
    } catch (e: Exception) {
        PicPlug.logger.error("${e.javaClass.name} : $e.message")
        // 若发生网络异常，则进行有限次的重试
        downloadImg(url, imagePath, tryCount - 1)
    } else PicType.UNKNOWN to "err"
}

fun convertWebpToPng(webpFilePath: String, pngFilePath: String) {
    val webpFile = File(webpFilePath)

    // 读取 WebP 图像
    val webpImage = readWebpImage(webpFile)

    if (webpImage != null) {
        // 创建 PNG 文件
        val pngFile = File(pngFilePath)

        // 写入 PNG 图像
        writePngImage(pngFile, webpImage)

        println("成功转换 WebP 到 PNG: $webpFilePath -> $pngFilePath")
    } else {
        println("读取 WebP 图像失败: $webpFilePath")
    }

}

fun readWebpImage(webpFile: File): BufferedImage? {
    try {
        // 注册 TwelveMonkeys 的 WebP 读取器
        IIORegistry.getDefaultInstance().registerServiceProvider(WebPImageReaderSpi())

        // 创建 WebP 图像阅读器
        val reader = ImageIO.getImageReadersByFormatName("webp").next()

        // 设置输入文件
        reader.setInput(ImageIO.createImageInputStream(webpFile))

        // 读取图像
        return reader.read(0)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun writePngImage(pngFile: File, image: BufferedImage) {
    try {
        // 创建 PNG 图像写入器
        val writer = ImageIO.getImageWritersByFormatName("png").next()

        // 设置输出文件
        writer.output = ImageIO.createImageOutputStream(pngFile)

        // 写入图像
        writer.write(image)

        // 关闭写入器
        writer.dispose()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
