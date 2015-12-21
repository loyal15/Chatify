
package net.java.otr4j.io.messages;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import net.java.otr4j.OtrException;
import net.java.otr4j.test.dummyclient.DummyClient;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class DataMessageTest {

    String snippets[] = {
            "བོད་རིགས་ཀྱི་བོད་སྐད་བརྗོད་པ་དང་ བོད་རིགས་མང་ཆེ་བ་ནི་ནང་ཆོས་བྱེད་པ་དང་ འགའ་ཤས་བོན་ཆོས་བྱེད་ཀྱིན་ཡོད་ འགའ་ཤས་ཁ་ཆེའི་ཆོས་བྱེད་ཀྱིན་ཡོད། ནང་ཆོས་ཀྱིས་བོད་ཀྱི་སྒྱུ་རྩལ་དང་ཟློས་གར་ཁང་རྩིག་བཟོ་རིག་ལ་སོགས་ལ་ཤུགས་རྐྱེན་ཆེན་པོ་འཐེབ་ཀྱིན་ཡོད།",
            "تبتی قوم (Tibetan people) (تبتی: བོད་པ་، وائلی: Bodpa، چینی: 藏族؛ پنین: Zàng",
            "تبتی قوم سے ربط رکھنے والے صفحات",
            "Учените твърдят, че тибетците нямат проблеми с разредения въздух и екстремни студове, защото не са хора. Размус Нилсен от университета Бъркли и неговите сътрудници от лабораторията за ДНК изследвания в Китай твърдят, че тибетците",
            "Câung-cŭk (藏族, Câung-ngṳ̄: བོད་པ་) sê Câung-kṳ̆ (bău-guók gĭng-dáng gì Să̤-câung) gì siŏh ciáh mìng-cŭk, iâ sê Dṳ̆ng-guók guăng-huŏng giĕ-dêng gì „Dṳ̆ng-huà Mìng-cŭk“ cĭ ék.",
            "チベット系民族（チベットけいみんぞく）は、主としてユーラシア大陸中央部のチベット高原上に分布する民族で、モンゴロイドに属する。",
            "原始汉人与原始藏缅人约在公元前4000年左右分开。原始汉人逐渐移居到黄河流域从事农业，而原始藏缅人则向西南迁徙并从事游牧业。而之后藏族与缅族又进一步的分离。[1]原始藏缅人屬於古羌人系統，发羌入藏為吐蕃王朝發跡的一種歷史學觀點",
            "Տիբեթացիներ (ինքնանվանումը՝ պյոբա), ժողովուրդ, Տիբեթի արմատական բնակչությունը։ Բնակվում են Չինաստանում (Տիբեթի ինքնավար շրջան, Դանսու, Ցինհայ, Սըչուան, Ցուննան նահանգներ), որոշ մասը՝ Հնդկաստանում, Նեպալում և Բութանում։ Ընդհանուր թիվը՝ մոտ 5 մլն (1978)։ Խոսում ենտիբեթերենի բարբառներով։ Հիմնական կրոնը լամայականությունն է (բուդդայականության հյուսիսային ճյուղ)։ Տիբեթացիների կեսից ավելին լեռնային նստակյաց երկրագործներ են (աճեցնում են հիմնականում գարի, ցորեն, բրինձ), մնացածներրը՝ կիսանստակյաց հողագործ-անասնապահներ և թափառակեցիկ անասնապահներ (բուծում են եղնայծ, ձի, ոչխար, այծ)։ Զարգացած են արհեստները։ XX դ․ սկզբին ստեղծվել են արդիական մի քանի փոքր ձեռնարկություններ",
            "... Gezginci olarak yabancılarla karışanlar \"شْتَن Xotan\" ve \"تبت Tübüt\" halkı ile \"طَنغُت Tenğüt\"lerin bir kısmıdır.\"[1] ve \"Tübütlüler تبت adında birinin oğullarıdır. Bu, Yemenli bir kimsedir, orada birini öldürmüş, korkusundan kaçmış, bir gemiye binerek Çine gelmiş, \"Tibet\" ülkesi onun hoşuna gitmiş, orada yerleşmiş; çoluğu çocuğu çoğalmış, torunları Türk topraklarından bin beşyüz fersah yer almışlar, Çin ülkesi Tibetin doğu tarafındadır.\"[2] şeklinde yorumlar.",
            "Tibeťané jsou domorodí obyvatelé Tibetu a přilehlých oblastí Centrální Asie, počínaje Myanmarem na jihovýchodě a Čínskou lidovou republikou na východě konče. Počet Tibeťanů je těžko odhadnutelný, podle údajů Ústřední tibetské správy populace Tibeťanů klesla od roku 1959 z 6,3 milionů na 5,4 milionů",
            "ئاچاڭ مىللىتى - بەيزۇ مىللىتى - بونان مىللىتى - بۇلاڭ مىللىتى - بۇيى مىللىت - چوسون مىللىتى - داغۇر مىللىتى - دەيزۇ مىللىتى - دېئاڭ مىللىتى - دۇڭشياڭ مىللىتى - دۇڭزۇ مىللىتى - دۇلۇڭ مىللىتى - رۇس مىللىتى - ئورۇنچون مىللىتى - ئېۋېنكى مىللىتى - گېلاۋ مىللىتى - ھانى مىللىتى - قازاق مىللىتى - خېجى مىللىتى - خۇيزۇ مىللىتى - گاۋشەن مىللىتى - خەنزۇ مىللىتى - كىنو مىللىتى - جىڭزۇ مىللىتى - جخڭپو مىللىتى - قىرغىز مىللىتى - لاخۇ مىللىتى - لىزۇ مىللىتى - لىسۇ مىللىتى - لوبا مىللىتى - مانجۇ مىللىتى - ماۋنەن مىللىتى - مېنبا مىللىتى - موڭغۇل مىللىتى - مياۋزۇ مىللىتى - مۇلاۋ مىللىتى - ناشى مىللىتى - نۇزۇ مىللىتى - پۇمى مىللىتى - چياڭزۇ مىللىتى - سالار مىللىتى - شېزۇ مىللىتى - شۈيزۇلار - تاجىك مىللىتى - تاتار مىللىتى - تۇجيا مىللىتى - تۇزۇ مىللىتى - ۋازۇ مىللىتى - ئۇيغۇر مىللىتى - ئۆزبېك مىللىتى - شىبە مىللىتى - ياۋزۇ مىللىتى - يىزۇ مىللىتى - يۇغۇر مىللىتى - تىبەت مىللىتى - جۇاڭزۇ مىللىتى",
            "Miscellaneous Symbols and Pictographs[1][2]Official Unicode Consortium code chart (PDF)    0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   FU+1F30x 🌀  🌁  🌂  🌃  🌄  🌅  🌆  🌇  🌈  🌉  🌊  🌋  🌌  🌍  🌎  🌏U+1F31x 🌐  🌑  🌒  🌓  🌔  🌕  🌖  🌗  🌘  🌙  🌚  🌛  🌜  🌝  🌞  🌟U+1F32x 🌠  🌡  🌢  🌣  🌤  🌥  🌦  🌧  🌨  🌩  🌪  🌫  🌬         U+1F33x 🌰  🌱  🌲  🌳  🌴  🌵  🌶  🌷  🌸  🌹  🌺  🌻  🌼  🌽  🌾  🌿U+1F34x 🍀  🍁  🍂  🍃  🍄  🍅  🍆  🍇  🍈  🍉  🍊  🍋  🍌  🍍  🍎  🍏U+1F35x 🍐  🍑  🍒  🍓  🍔  🍕  🍖  🍗  🍘  🍙  🍚  🍛  🍜  🍝  🍞  🍟U+1F36x 🍠  🍡  🍢  🍣  🍤  🍥  🍦  🍧  🍨  🍩  🍪  🍫  🍬  🍭  🍮  🍯U+1F37x 🍰  🍱  🍲  🍳  🍴  🍵  🍶  🍷  🍸  🍹  🍺  🍻  🍼  🍽     U+1F38x 🎀  🎁  🎂  🎃  🎄  🎅  🎆  🎇  🎈  🎉  🎊  🎋  🎌  🎍  🎎  🎏U+1F39x 🎐  🎑  🎒  🎓  🎔  🎕  🎖  🎗  🎘  🎙  🎚  🎛  🎜  🎝  🎞  🎟U+1F3Ax 🎠  🎡  🎢  🎣  🎤  🎥  🎦  🎧  🎨  🎩  🎪  🎫  🎬  🎭  🎮  🎯U+1F3Bx 🎰  🎱  🎲  🎳  🎴  🎵  🎶  🎷  🎸  🎹  🎺  🎻  🎼  🎽  🎾  🎿U+1F3Cx 🏀  🏁  🏂  🏃  🏄  🏅  🏆  🏇  🏈  🏉  🏊  🏋  🏌  🏍  🏎 U+1F3Dx                 🏔  🏕  🏖  🏗  🏘  🏙  🏚  🏛  🏜  🏝  🏞  🏟U+1F3Ex 🏠  🏡  🏢  🏣  🏤  🏥  🏦  🏧  🏨  🏩  🏪  🏫  🏬  🏭  🏮  🏯U+1F3Fx 🏰  🏱  🏲  🏳  🏴  🏵  🏶  🏷                             U+1F40x 🐀  🐁  🐂  🐃  🐄  🐅  🐆  🐇  🐈  🐉  🐊  🐋  🐌  🐍  🐎  🐏U+1F41x 🐐  🐑  🐒  🐓  🐔  🐕  🐖  🐗  🐘  🐙  🐚  🐛  🐜  🐝  🐞  🐟U+1F42x 🐠  🐡  🐢  🐣  🐤  🐥  🐦  🐧  🐨  🐩  🐪  🐫  🐬  🐭  🐮  🐯U+1F43x 🐰  🐱  🐲  🐳  🐴  🐵  🐶  🐷  🐸  🐹  🐺  🐻  🐼  🐽  🐾  🐿U+1F44x 👀  👁  👂  👃  👄  👅  👆  👇  👈  👉  👊  👋  👌  👍  👎  👏U+1F45x 👐  👑  👒  👓  👔  👕  👖  👗  👘  👙  👚  👛  👜  👝  👞  👟U+1F46x 👠  👡  👢  👣  👤  👥  👦  👧  👨  👩  👪  👫  👬  👭  👮  👯U+1F47x 👰  👱  👲  👳  👴  👵  👶  👷  👸  👹  👺  👻  👼  👽  👾  👿U+1F48x 💀  💁  💂  💃  💄  💅  💆  💇  💈  💉  💊  💋  💌  💍  💎  💏U+1F49x 💐  💑  💒  💓  💔  💕  💖  💗  💘  💙  💚  💛  💜  💝  💞  💟U+1F4Ax 💠  💡  💢  💣  💤  💥  💦  💧  💨  💩  💪  💫  💬  💭  💮  💯U+1F4Bx 💰  💱  💲  💳  💴  💵  💶  💷  💸  💹  💺  💻  💼  💽  💾  💿U+1F4Cx 📀  📁  📂  📃  📄  📅  📆  📇  📈  📉  📊  📋  📌  📍  📎  📏U+1F4Dx 📐  📑  📒  📓  📔  📕  📖  📗  📘  📙  📚  📛  📜  📝  📞  📟U+1F4Ex 📠  📡  📢  📣  📤  📥  📦  📧  📨  📩  📪  📫  📬  📭  📮  📯U+1F4Fx 📰  📱  📲  📳  📴  📵  📶  📷  📸  📹  📺  📻  📼  📽  📾 U+1F50x 🔀  🔁  🔂  🔃  🔄  🔅  🔆  🔇  🔈  🔉  🔊  🔋  🔌  🔍  🔎  🔏U+1F51x 🔐  🔑  🔒  🔓  🔔  🔕  🔖  🔗  🔘  🔙  🔚  🔛  🔜  🔝  🔞  🔟U+1F52x 🔠  🔡  🔢  🔣  🔤  🔥  🔦  🔧  🔨  🔩  🔪  🔫  🔬  🔭  🔮  🔯U+1F53x 🔰  🔱  🔲  🔳  🔴  🔵  🔶  🔷  🔸  🔹  🔺  🔻  🔼  🔽  🔾  🔿U+1F54x 🕀  🕁  🕂  🕃  🕄  🕅  🕆  🕇  🕈  🕉  🕊                 U+1F55x 🕐  🕑  🕒  🕓  🕔  🕕  🕖  🕗  🕘  🕙  🕚  🕛  🕜  🕝  🕞  🕟U+1F56x 🕠  🕡  🕢  🕣  🕤  🕥  🕦  🕧  🕨  🕩  🕪  🕫  🕬  🕭  🕮  🕯U+1F57x 🕰  🕱  🕲  🕳  🕴  🕵  🕶  🕷  🕸  🕹      🕻  🕼  🕽  🕾  🕿U+1F58x 🖀  🖁  🖂  🖃  🖄  🖅  🖆  🖇  🖈  🖉  🖊  🖋  🖌  🖍  🖎  🖏U+1F59x 🖐  🖑  🖒  🖓  🖔  🖕  🖖  🖗  🖘  🖙  🖚  🖛  🖜  🖝  🖞  🖟U+1F5Ax 🖠  🖡  🖢  🖣      🖥  🖦  🖧  🖨  🖩  🖪  🖫  🖬  🖭  🖮  🖯U+1F5Bx 🖰  🖱  🖲  🖳  🖴  🖵  🖶  🖷  🖸  🖹  🖺  🖻  🖼  🖽  🖾  🖿U+1F5Cx 🗀  🗁  🗂  🗃  🗄  🗅  🗆  🗇  🗈  🗉  🗊  🗋  🗌  🗍  🗎  🗏U+1F5Dx 🗐  🗑  🗒  🗓  🗔  🗕  🗖  🗗  🗘  🗙  🗚  🗛  🗜  🗝  🗞  🗟U+1F5Ex 🗠  🗡  🗢  🗣  🗤  🗥  🗦  🗧  🗨  🗩  🗪  🗫  🗬  🗭  🗮  🗯U+1F5Fx 🗰  🗱  🗲  🗳  🗴  🗵  🗶  🗷  🗸  🗹  🗺  🗻  🗼  🗽  🗾  🗿",
            "😀 😁  😂  😃  😄  😅  😆  😇  😈  😉  😊  😋  😌  😍  😎  😏U+1F61x 😐  😑  😒  😓  😔  😕  😖  😗  😘  😙  😚  😛  😜  😝  😞  😟U+1F62x 😠  😡  😢  😣  😤  😥  😦  😧  😨  😩  😪  😫  😬  😭  😮  😯U+1F63x 😰  😱  😲  😳  😴  😵  😶  😷  😸  😹  😺  😻  😼  😽  😾  😿U+1F64x 🙀  🙁  🙂          🙅  🙆  🙇  🙈  🙉  🙊  🙋  🙌  🙍  🙎  🙏",
            "🌀🌁🌂🌃🌄🌅🌆🌇🌈🌉🌊🌋🌌🌍🌎🌏🌐🌑🌒🌓🌔🌕🌖🌗🌘🌙🌚🌛🌜🌝🌞🌟🌠 🌰🌱🌲🌳🌴🌵🌷🌸🌹🌺🌻🌼🌽🌾🌿🍀🍁🍂🍃🍄🍅🍆🍇🍈🍉🍊🍋🍌🍍🍎🍏🍐🍑🍒🍓🍔🍕🍖🍗🍘🍙🍚🍛🍜🍝🍞🍟 🍠🍡🍢🍣🍤🍥🍦🍧🍨🍩🍪🍫🍬🍭🍮🍯🍰🍱🍲🍳🍴🍵🍶🍷🍸🍹🍺🍻🍼🎀🎁🎂🎃🎄🎅🎆🎇🎈🎉🎊🎋🎌🎍🎎🎏🎐🎑🎒🎓 🎠🎡🎢🎣🎤🎥🎦🎧🎨🎩🎪🎫🎬🎭🎮🎯🎰🎱🎲🎳🎴🎵🎶🎷🎸🎹🎺🎻🎼🎽🎾🎿🏀🏁🏂🏃🏄🏅🏆🏇🏈🏉🏊 🏠🏡🏢🏣🏤🏥🏦🏧🏨🏩🏪🏫🏬🏭🏮🏯🏰🐀🐁🐂🐃🐄🐅🐆🐇🐈🐉🐊🐋🐌🐍🐎🐏🐐🐑🐒🐓🐔🐕🐖🐗🐘🐙🐚🐛🐜🐝🐞🐟 🐠🐡🐢🐣🐤🐥🐦🐧🐨🐩🐪🐫🐬🐭🐮🐯🐰🐱🐲🐳🐴🐵🐶🐷🐸🐹🐺🐻🐼🐽🐾👀👂👃👄👅👆👇👈👉👊👋👌👍👎👏 👐👑👒👓👔👕👖👗👘👙👚👛👜👝👞👟👠👡👢👣👤👥👦👧👨👩👪👫👬👭👮👯👰👱👲👳👴👵👶👷👸👹👺👻👼👽👾👿 💀💁💂💃💄💅💆💇💈💉💊💋💌💍💎💏💐💑💒💓💔💕💖💘💙💚💛💜💝💞💟💠💡💢💣💤💥💦💧💨💩💪💫💬💭💮💯 💰💱💲💳💴💵💶💷💸💹💺💻💼💽💾💿📀📁📂📃📄📅📆📇📈📉📊📋📌📍📎📏📐📑📒📓📔📕📖📗📘📙📚📛📜📝📞📟 📠📡📢📣📤📥📦📧📨📩📪📫📬📭📮📯📰📱📲📳📴📵📶📷📹📺📻📼🔀🔁🔂🔃🔄🔅🔆🔇🔈🔉🔊🔋🔌🔍🔎🔏 🔐🔑🔒🔓🔔🔕🔖🔗🔘🔙🔚🔛🔜🔝🔞🔟🔠🔡🔢🔣🔤🔥🔦🔧🔨🔩🔪🔫🔬🔭🔮🔯🔰🔱🔲🔳🔴🔵🔶🔷🔸🔹🔺🔻🔼🔽 🕐🕑🕒🕓🕔🕕🕖🕗🕘🕙🕚🕛🕜🕝🕞🕟🕠🕡🕢🕣🕤🕥🕦🕧🗻🗼🗽🗾🗿 😁😂😃😄😅😆😇😈😉😊😋😌😍😎😏😐😒😓😔😖😘😚😜😝😞😠😡😢😣😤😥😨😩😪😫😭😰😱😲😳😵😶😷 😸😹😺😻😼😽😾😿🙀🙅🙆🙇🙈🙉🙊🙋🙌🙍🙎🙏 🚀🚁🚂🚃🚄🚅🚆🚇🚈🚉🚊🚋🚌🚍🚎🚏🚐🚑🚒🚓🚔🚕🚖🚗🚘🚙🚚🚛🚜🚝🚞🚟🚠🚡🚢🚣🚤🚥🚦🚧🚨🚩🚪 🚫🚬🚭🚮🚯🚰🚱🚲🚳🚴🚵🚶🚷🚸🚹🚺🚻🚼🚽🚾🚿🛀🛁🛂🛃🛄🛅",
            "Royal Thai (ราชาศัพท์): (influenced by Khmer) used when addressing members of the royal family or describing their activities. ",
            "טיילאנדיש (ภาษาไทย) איז די באַאַמטער שפּראַך פון טיילאנד און די טייַלענדיש מענטשן. 20,000,000 מענטשן רעדן די שפּראַך, פון זיי -4,700,000 רעדן זי ווי זייער מוטערשפראך.",
            "the Khmer term is ជើងអក្សរ cheung âksâr, meaning \"foot of a letter\"",
            "중화인민공화국에서는 기본적으로 한족은 1명, 일반 소수민족은 2명까지 낳을 수 있지만 3000m 이상의 산지나 고원에서 사는 티베트족은 3명까지 낳을 수 있다",
            "पाठ्यांशः अत्र उपलभ्यतेसर्जनसामान्यलक्षणम्/Share-Alike License; अन्ये नियमाः आन्विताः भवेयुः । दृश्यताम्Terms of use अधिकविवरणाय ।",
            "থাইল্যান্ডের প্রায় ২ কোটি লোকের মাতৃভাষা থাই, যা থাইল্যান্ডের জাতীয় ভাষা। এছাড়া দ্বিতীয় ভাষা হিসেবে আরও প্রায় ২ কোটি লোক আদর্শ থাই ভাষাতে কথা বলতে পারেন। থাইল্যান্ড ছাড়াও মিডওয়ে দ্বীপপুঞ্জ, সিঙ্গাপুর, সংযুক্ত আরব আমিরাত এবং মার্কিন যুক্তরাষ্ট্রে থাই ভাষা প্রচলিত। থাই ভাষাতে \"থাই\" শব্দটির অর্থ \"স্বাধীনতা\"।",
            "திபெத்துக்கு வெளியே வாழும் திபெத்தியர்கள் தெரிவிக்கிறார்கள்",
            "អក្សរសាស្រ្តខែ្មរមានប្រវ៌ត្តជាងពីរពាន់ឆ្នាំមកហើយ ចាប់តាំងពីកំនើតប្រទេសខែ្មរដំបូងមកម្លោះ។ ជនជាតិខែ្មរសម៍យបុរាណបានសំរួលអក្សរខ្មែរមរពីអក្សរសំស្ក្រឹត។",
            "촇֊儠蛸ᣞ㎧贲웆꘠샾䛱郣굉ᵏ椚⣦赢霯⟜㜈幫틃㭯㝻㖎즋鶚宬㑍黡ㆇར렀네𩗗ᄉᄔ嚖蒙⚙摍⨔裔쐬䈇⩌휥㱱蔿⺌ꂤ󌐓쌹᳛쯀汣使ⶓ昌沐꽔⟰錉𨴃⤋冖땀歷皼缔㉚旮쑗匎˺硚鈈ၕ凣碁蜨嬣ᬯ",
            "㢐򇐫큨败奊惆꘤쀉狨㏲㿯뇢縿ꅀ턺䆽靏鱸ꖽ圼І๠㊷槥岾鑨鬦𫭪뵝韻ᒢ覲ڸ巈󡡡虷빉鴟ｵ듷쁼ẓ➱淨㖌甩⦼躂௬ဃ젃扒䠾ㄱ뗄஄䶁늪닫伆牞Ｊ",
    };
    String whackNullSnippets[] = {
            "asdf\0\0",
            "\0\0\0\0\0\0\0",
            "asdfasdf\0\0aadsfasdfa\0",
            "\0\0អក្សរសាស្រ្តខែ្មរមានប្រវ៌ត្តជាងពីរពាន់ឆ្នាំមកហើយ",
    };

    @Test
    public void testWhackUnicodeWithNull() throws OtrException {
        String msg = null;
        DummyClient[] convo = DummyClient.getConversation();
        DummyClient alice = convo[0];
        DummyClient bob = convo[1];
        DummyClient.forceStartOtr(alice, bob);

        for (int i = 0; i < 100; i++) {
            msg = snippets[RandomUtils.nextInt(0, whackNullSnippets.length - 1)];
            alice.send(bob.getAccount(), msg);
            assertThat("Message has been transferred encrypted.",
                    alice.getConnection().getSentMessage(),
                    not(equalTo(msg)));
            assertEquals("Received message should match sent message.",
                    // remove nulls like SerializationUtils does
                    msg.replace('\0', '?'),
                    bob.pollReceivedMessage().getContent());

            msg = snippets[RandomUtils.nextInt(0, whackNullSnippets.length - 1)];
            bob.send(alice.getAccount(), msg);
            assertThat("Message has been transferred encrypted.",
                    bob.getConnection().getSentMessage(),
                    not(equalTo(msg)));
            assertEquals("Received message should match sent message.",
                    // remove nulls like SerializationUtils does
                    msg.replace('\0', '?'),
                    alice.pollReceivedMessage().getContent());
        }
        bob.exit();
        alice.exit();
    }

    @Test
    public void testWithRandomlyGeneratedUnicode() throws OtrException {
        String msg = null;
        String sent;
        String received;
        DummyClient[] convo = DummyClient.getConversation();
        DummyClient alice = convo[0];
        DummyClient bob = convo[1];
        DummyClient.forceStartOtr(alice, bob);

        for (int i = 0; i < 1000; i++) {
            int aliceSize = RandomUtils.nextInt(0, 100000);
            msg = RandomStringUtils.random(aliceSize);
            alice.send(bob.getAccount(), msg);
            sent = alice.getConnection().getSentMessage();
            assertThat("Message has been transferred encrypted.", sent, not(equalTo(msg)));
            received = bob.pollReceivedMessage().getContent();
            assertEquals("String lengths should be equal", msg.length(), received.length());
            assertEquals("Received message should match sent message.",
                    // remove nulls like SerializationUtils does
                    msg.replace('\0', '?'),
                    received);

            int bobSize = RandomUtils.nextInt(0, 100000);
            msg = RandomStringUtils.random(bobSize);
            bob.send(alice.getAccount(), msg);
            sent = bob.getConnection().getSentMessage();
            assertThat("Message has been transferred encrypted.", sent, not(equalTo(msg)));
            received = alice.pollReceivedMessage().getContent();
            assertEquals("String lengths should be equal", msg.length(), received.length());
            assertEquals("Received message should match sent message.",
                    // remove nulls like SerializationUtils does
                    msg.replace('\0', '?'),
                    received);
        }
        bob.exit();
        alice.exit();
    }

    @Test
    public void testForceStartWithHardCodedSnippets() throws OtrException {
        String msg;
        DummyClient[] convo = DummyClient.getConversation();
        DummyClient alice = convo[0];
        DummyClient bob = convo[1];
        DummyClient.forceStartOtr(alice, bob);

        for (int i = 0; i < 100; i++) {
            msg = snippets[RandomUtils.nextInt(0, snippets.length - 1)];
            alice.send(bob.getAccount(), msg);
            assertThat("Message has been transferred encrypted.",
                    alice.getConnection().getSentMessage(),
                    not(equalTo(msg)));
            assertEquals("Received message is different from the sent message.",
                    msg, bob.pollReceivedMessage().getContent());
            msg = snippets[RandomUtils.nextInt(0, snippets.length - 1)];
            bob.send(alice.getAccount(), msg);
            assertThat("Message has been transferred encrypted.",
                    bob.getConnection().getSentMessage(),
                    not(equalTo(msg)));
            assertEquals("Received message is different from the sent message.",
                    msg, alice.pollReceivedMessage().getContent());
        }
        bob.exit();
        alice.exit();
    }

    @Test
    public void testDummyClientWithHardCodedSnippets() throws OtrException {
        String msg;
        DummyClient[] convo = DummyClient.getConversation();
        DummyClient alice = convo[0];
        DummyClient bob = convo[1];

        for (int i = 0; i < 100; i++) {
            msg = snippets[RandomUtils.nextInt(0, snippets.length - 1)];
            alice.send(bob.getAccount(), msg);
            assertThat("plain transfer via DummyClient",
                    alice.getConnection().getSentMessage(),
                    equalTo(msg));
            assertEquals("Received message is different from the sent message.",
                    msg, bob.pollReceivedMessage().getContent());
            msg = snippets[RandomUtils.nextInt(0, snippets.length - 1)];
            bob.send(alice.getAccount(), msg);
            assertThat("plain transfer via DummyClient",
                    bob.getConnection().getSentMessage(),
                    equalTo(msg));
            assertEquals("Received message is different from the sent message.",
                    msg, alice.pollReceivedMessage().getContent());
        }
        bob.exit();
        alice.exit();
    }
}
