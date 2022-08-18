package org.fujiwara.shirakana.adminbot.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.announcement.OfflineAnnouncement
import net.mamoe.mirai.contact.announcement.buildAnnouncementParameters
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.fujiwara.shirakana.adminbot.*
import org.fujiwara.shirakana.adminbot.configAndData.*
import java.awt.Font
import java.awt.image.*
import java.io.File
import java.net.URL
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.*


fun rotateImage(image: BufferedImage, theta: Double): BufferedImage? {
    val width = image.width
    val height = image.height
    val angle = theta * Math.PI / 180 // 度转弧度
    val resultImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    for (i in 0 until width) {
        for (j in 0 until height) {
            var x = i - width / 2
            var y = height / 2 - j
            val radius = sqrt((x * x + y * y).toDouble())
            var angle1: Double = if (y > 0) {
                acos(x / radius)
            } else {
                2 * Math.PI - acos(x / radius)
            }
            x = (radius * cos(angle1 - angle)).toInt()
            y = (radius * sin(angle1 - angle)).toInt()
            if ((x < width / 2) and (x > -(width / 2)) and (y < height / 2) and (y > -(height / 2))) {
                val rgb = image.getRGB(x + width / 2, height / 2 - y)
                resultImage.setRGB(i, j, rgb)
            } else {

            }
        }
    }
    return resultImage
}

object ShirakanaParanoia : CompositeCommand(
    ShirakanaAdminBot, "Paranoia",
    description = "偏执度相关的操作",
) {
    @SubCommand
    @Description("增加指定数量的偏执度")
    suspend fun CommandSender.add(Amount: Long) {
        if(Amount<=0){return}
        var tempParanoia = ShirakanaDataGroupMember.bigCleanParanoia
        tempParanoia += Amount
        if(tempParanoia > 100){
            tempParanoia = 100
        }
        ShirakanaDataGroupMember.bigCleanParanoia = tempParanoia
        sendMessage("当前偏执度${tempParanoia}")
    }
    @SubCommand
    @Description("减少指定数量的偏执度")
    suspend fun CommandSender.decrease(Amount: Long) {
        if(Amount<=0){return}
        var tempParanoia = ShirakanaDataGroupMember.bigCleanParanoia
        tempParanoia -= Amount
        if(tempParanoia < 0){
            tempParanoia = 0
        }
        ShirakanaDataGroupMember.bigCleanParanoia = tempParanoia
        sendMessage("当前偏执度${tempParanoia}")
    }
    @SubCommand
    @Description("查看管理偏执度数值")
    suspend fun CommandSender.reload() {
        if(bot==null||subject==null){
            return
        }
        val group1 = bot?.getGroup(subject!!.id) ?: return
        val GroupID = group1.id
        if (group1?.botAsMember!!.isAdministrator() || group1.botAsMember.isOwner()) {
            if (ShirakanaDataFlags.announcementsList.contains(GroupID.toLong())) {
                ShirakanaDataFlags.announcementsList[GroupID.toLong()]?.let {
                    group1.announcements.delete(
                        it
                    )
                }
            }
            val imageAnnouncementTmp =
                group1.announcements.uploadImage(ShirakanaParanoia.paranoiaImage(group1))
            val parameterTmp = buildAnnouncementParameters {
                image = imageAnnouncementTmp
                sendToNewMember = false
                isPinned = false
                showEditCard = false
                showPopup = false
                requireConfirmation = false
            }
            val announcementTemp = OfflineAnnouncement.create("管理偏执度", parameterTmp)
            if (!ShirakanaDataFlags.announcementsList.contains(GroupID.toLong())) {
                ShirakanaDataFlags.announcementsList.put(
                    GroupID.toLong(),
                    announcementTemp.publishTo(group1).fid
                )
            } else {
                ShirakanaDataFlags.announcementsList[GroupID.toLong()] =
                    announcementTemp.publishTo(group1).fid
            }
        }
    }

    @JvmStatic
    val stalinAKImg: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Img.png"))
    @JvmStatic
    val ParanoiaIMG: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Paranoia.png"))
    @JvmStatic
    val ParanoiaBg: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Paranoia-bg.png"))
    @JvmStatic
    val ParanoiaEmpty: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Paranoia-empty.png"))
    @JvmStatic
    val ParanoiaFull: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Paranoia-full.png"))
    @JvmStatic
    val ParanoiaTarget: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("target.png"))


    fun paranoiaImage(group : Group): ExternalResource{
        val newImg = BufferedImage(1280, 800, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics = newImg.createGraphics()
        val newImg2 = BufferedImage(1230, 800, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics2 = newImg.createGraphics()

        graphics.drawImage(ParanoiaEmpty, 0, 0, null)
        val thisFileTmp = File("data/org.fujiwara.shirakana.adminbot.plugin/cleaned")
        val imageSetTmp = mutableSetOf<BufferedImage>()
        if (thisFileTmp.exists()){
            for(fileTmp in thisFileTmp.listFiles()){
                imageSetTmp.add(ImageIO.read(fileTmp))
            }
        }
        val it = imageSetTmp.iterator()
        if(it.hasNext()){
            graphics.drawImage(it.next(), 49, 281,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 211, 267,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 366, 262,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 80, 515,150,150, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 274, 499,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 858, 315,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 1135, 264,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 939, 548,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 1109, 608,140,140, null)
        }
        graphics.drawImage(ParanoiaTarget,0,0,null)
        graphics.drawImage(rotateImage(ParanoiaFull,-1.8*ShirakanaDataGroupMember.bigCleanParanoia), 490, 429, null)
        graphics.drawImage(ParanoiaBg, 0, 0, null)
        var font = Font("黑体", Font.PLAIN, 38)
        graphics.font = font
        graphics.drawString("管理偏执度",533,72)
        font = Font("黑体", Font.PLAIN, 31)
        graphics2.font = font
        val str = "一种怀疑和不信任的气氛笼罩着"+group.name+"。随着内务人民委员会对不计其数的怠工、"
        val str2 = "破坏及间谍行为的指控，"+group.owner.nick+"对群内派系斗争的担忧与日俱增"
        graphics2.drawString(str,25,155)
        graphics2.drawString(str2,25,186)
        graphics2.dispose()
        graphics.drawImage(newImg2,25,127,null)
        graphics.drawImage(rotateImage(ParanoiaIMG,-1.89*ShirakanaDataGroupMember.bigCleanParanoia),516,441,null)
        font = Font("黑体", Font.PLAIN, 39)
        graphics.font = font
        graphics.drawString("管理偏执度: "+ShirakanaDataGroupMember.bigCleanParanoia.toString()+"%",516,735)
        graphics.dispose()
        val uuidFileName="shirakana/"+UUID.randomUUID().toString().replace("-","")+".png"
        val outPutImage = File(uuidFileName)
        ImageIO.write(newImg, "png", outPutImage)
        return outPutImage.toExternalResource()
    }

    suspend fun GetImageTutu(target : Contact,group : Contact):Image{
        return KillGroupMembersImage(withContext(Dispatchers.IO) {
            ImageIO.read(URL(target.avatarUrl))
        }).uploadAsImage(group)
    }

    fun KillGroupMembersImage(imageMembers: BufferedImage): ExternalResource {
        val newImg = BufferedImage(600, 349, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics = newImg.createGraphics()
        graphics.drawImage(imageMembers, 29, 22, 154, 154, null)
        graphics.drawImage(stalinAKImg, 0, 0, null)
        graphics.dispose()
        val uuidFileName="shirakana/"+UUID.randomUUID().toString().replace("-","")+".png"
        val outPutImage = File(uuidFileName)
        ImageIO.write(newImg, "png", outPutImage)
        return outPutImage.toExternalResource()
    }
}

object ShirakanaXianZhongRen : CompositeCommand(
    ShirakanaAdminBot, "Tutu","清洗",
    description = "开启献忠人模式",
) {
    @SubCommand
    @Description("大清洗")
    suspend fun CommandSender.BigClean(){
        sendMessage("输入/Tutu StartBigCleanStartBigClean <群号> <确认群号>来进行大清洗\n警告！！！！！！！\n大清洗功能会清理60天内未发言的所有群友（排除大清洗保护名单、有专属头衔、群备注中包含标准命名规则的人），该清洗可能会是几十甚至几百的量级且无法撤回，请仔细斟酌是否执行")
    }
    @SubCommand
    @Description("确认大清洗")
    suspend fun CommandSender.StartBigCleanStartBigClean(GroupTarget: String,ConfirmGroup: String) {
        if(ShirakanaBigCleanSetting.nameStandard==""){
            sendMessage("未配置ShirakanaBigCleanSetting.nameStandard（群名标准），请前往config设置")
            return
        }
        if(GroupTarget==""||ConfirmGroup==""||(GroupTarget!=ConfirmGroup)){
            sendMessage("请再次思考后输入正确的参数")
            return
        }else{
            if(!ShirakanaDataGroupMember.enabledGroups.contains(GroupTarget.toLong())){
                sendMessage("该群聊未启用")
                return
            }else{
                val currGroupThis = bot?.getGroup(GroupTarget.toLong())
                if (currGroupThis != null) {
                    for(currMember in currGroupThis.members){
                        if(currMember.lastSpeakTimestamp>=ShirakanaBigCleanSetting.bigCleanTime*24*60*60*100) {
                            if (ShirakanaDataGroupMember.bigCleanWhiteList.contains(currMember.id)) {
                                continue
                            }
                            if (currMember.specialTitle != "") {
                                continue
                            }
                            if (currMember.nameCard.contains(ShirakanaBigCleanSetting.nameStandard)) {
                                continue
                            }
                            if(currMember.isAdministrator()||currMember.isOwner()){
                                continue
                            }
                            else{
                                currMember.kick("您由于过长时间不发言且未注明理由被请出群聊，如果误踢请重新加入，对此造成的麻烦我们深表歉意。")
                            }
                        }
                    }
                }
            }
            ShirakanaDataGroupMember.bigCleanParanoia=0L
        }
    }
    @SubCommand
    @Description("查看处于被清洗状态的QQ号码")
    suspend fun CommandSender.List(){
        sendMessage("被清洗的QQ："+ShirakanaDataGroupMember.blackList)
    }
    @SubCommand
    @Description("将一个账号添加至“被清洗”状态列表")
    suspend fun CommandSender.Add(QQId : Long){
        var targets=""
        for (groupId in ShirakanaDataGroupMember.enabledGroups) {
            val tmpGroup = bot?.getGroup(groupId) ?: continue
            val memberTmp = tmpGroup.members[QQId] ?: continue
            if(ShirakanaDataGroupMember.blackList.add(QQId)) {
                targets += "昵称：" + memberTmp.nick + " QQ号：" + memberTmp.id + "\n"
            }
            break
        }
        if (targets==""){
            sendMessage("列表中无新增成员")
        }else{
            sendMessage("以下群友被加入清洗名单：\n$targets")
        }
    }
    @SubCommand
    @Description("将一个账号移除出“被清洗”状态列表")
    suspend fun CommandSender.Del(QQId : Long){
        var targets=""
        for (groupId in ShirakanaDataGroupMember.enabledGroups) {
            val tmpGroup = bot?.getGroup(groupId) ?: continue
            val memberTmp = tmpGroup.members[QQId] ?: continue
            if(ShirakanaDataGroupMember.blackList.remove(QQId)) {
                targets += "昵称：" + memberTmp.nick + " QQ号：" + memberTmp.id + "\n"
                val thisFileName = "data/org.fujiwara.shirakana.adminbot.plugin/cleaned/$QQId.png"
                val delPutImage = File(thisFileName)
                delPutImage.delete()
            }
            break
        }
        if (targets==""){
            sendMessage("列表中无减少成员")
        }else{
            sendMessage("以下群友被移除出清洗名单：\n$targets")
        }
    }
    @SubCommand
    @Description("清洗处于“被清洗”状态的成员")
    suspend fun CommandSender.kick(){
        for(groupId in ShirakanaDataGroupMember.enabledGroups){
            val groupTmp = bot?.getGroup(groupId) ?: continue
            for(memberId in ShirakanaDataGroupMember.blackList){
                groupTmp[memberId]?.kick("你已被清洗")
            }
        }
        sendMessage("清洗完毕")
    }

}

object QuickCleanRepeatTarget : SimpleCommand(
    ShirakanaAdminBot, "CleanRepeat",
    description = "清理被禁止重复加群的群员（将其保留在指定群，其他群踢出）"
) {
    @Handler
    suspend fun CommandSender.quickCleanRepeatTarget(GroupTarget: Long) {
        if(ShirakanaDataGroupMember.enabledGroups.contains(GroupTarget)&&bot?.getGroup(GroupTarget)!=null){
            for(groupId in ShirakanaDataGroupMember.enabledGroups){
                if(groupId==GroupTarget)continue
                val groupTmp = bot?.getGroup(GroupTarget) ?: continue
                if(ShirakanaBigCleanSetting.refuseMode){
                    for(memberId in ShirakanaDataGroupMember.repeatList){
                        groupTmp[memberId]?.kick("你重复加入了另一个群聊")
                    }
                }else{
                    for(member in groupTmp.members){
                        if(!ShirakanaDataGroupMember.repeatList.contains(member.id))
                            member.kick("你重复加入了另一个群聊")
                    }
                }
            }
        }
    }
}

object QBAMember : SimpleCommand(
    ShirakanaAdminBot, "QB",
    description = "枪毙一个群友"
) {
    @Handler
    suspend fun CommandSender.QBYGQY(MemberTarget: Member) {
        if(subject!=null&&ShirakanaDataGroupMember.enabledGroups.contains(subject!!.id)){
            if(MemberTarget.isAdministrator()||MemberTarget.isOwner()){
                subject!!.sendMessage("你无权清洗管理员")
                return
            }
            val msgChain = buildMessageChain {
                +PlainText("群友："+MemberTarget.nick+"已被清洗\n")
                +ShirakanaParanoia.GetImageTutu(MemberTarget,subject!!)
            }
            val newImg = withContext(Dispatchers.IO) {
                ImageIO.read(URL(MemberTarget.avatarUrl))
            }
            val thisFileName = "data/org.fujiwara.shirakana.adminbot.plugin/cleaned/"+MemberTarget.id.toString()+".png"
            val outPutImage = File(thisFileName)
            withContext(Dispatchers.IO) {
                ImageIO.write(newImg, "png", outPutImage)
            }
            for(groupID in ShirakanaDataGroupMember.enabledGroups){
                bot?.getGroup(groupID.toLong())?.sendMessage(msgChain)
                bot?.getGroup(groupID.toLong())?.get(MemberTarget.id)?.kick("你已被清洗")
            }
            ShirakanaDataGroupMember.blackList.add(MemberTarget.id)
        }
    }
}
/*

@SubCommand
    @Description("清洗一个指定群员（可用@）")
    suspend fun CommandSender.kill(TmpMember : Member){
        for(groupId in ShirakanaDataGroupMember.selectedGroups){
            val thisGroupTmp = bot?.getGroup(groupId.toLong())
            if(thisGroupTmp?.get(TmpMember.id) != null){
                if(TmpMember.isAdministrator()||TmpMember.isOwner()){
                    sendMessage("你无权清洗管理员")
                    return
                }
                val msgChain = buildMessageChain {
                    +PlainText("群友："+TmpMember.nick+"已被清洗\n")
                    +ShirakanaParanoia.GetImageTutu(TmpMember,thisGroupTmp)
                }
                thisGroupTmp.sendMessage(msgChain)
                val newImg = ImageIO.read(URL(TmpMember.avatarUrl))
                val thisFileName = "data/org.fujiwara.shirakana.adminbot.plugin/cleaned/"+TmpMember.id.toString()+".png"
                val outPutImage = File(thisFileName)
                ImageIO.write(newImg, "png", outPutImage)
                ShirakanaDataGroupMember.ShirakanaBlackListGroup.add(TmpMember.id.toString())
                thisGroupTmp.get(TmpMember.id)?.kick("你已被清洗")
            }
        }
        sendMessage("清洗完毕")
    }

*/