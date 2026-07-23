package com.releasecanvas.app.data.pdf

import com.releasecanvas.app.data.locale.AppLocale

/**
 * Versioned model-release templates by language. Not legal advice.
 * Naming like “500px-style” means inspired / unofficial.
 * Persian (fa) wording follows common Iranian model-release practice;
 * still unofficial samples, not attorney-certified.
 */
object ReleaseTerms {

    fun body(
        template: ReleaseTemplate,
        modelName: String,
        photographerName: String,
        languageTag: String = "en",
    ): String {
        val model = modelName.trim()
        val photographer = photographerName.trim()
        val lang = AppLocale.normalizeReleaseTag(languageTag)
        val raw = textFor(template, lang, model, photographer)
        return raw.trim().replace(Regex("\\s+"), " ")
    }

    private fun textFor(
        template: ReleaseTemplate,
        lang: String,
        model: String,
        photographer: String,
    ): String {
        val version = template.version
        val map = when (template) {
            ReleaseTemplate.GENERIC -> generic_bodies
            ReleaseTemplate.FIVE_HUNDRED_PX_STYLE -> five_hundred_px_style_bodies
            ReleaseTemplate.STOCK_RF_STYLE -> stock_rf_style_bodies
            ReleaseTemplate.EDITORIAL_ONLY -> editorial_only_bodies
            ReleaseTemplate.SOCIAL_WEB -> social_web_bodies
        }
        val pattern = map[lang] ?: map.getValue("en")
        return pattern
            .replace("{model}", model)
            .replace("{photographer}", photographer)
            .replace("{version}", version)
    }

    private val generic_bodies: Map<String, String> = mapOf(
        "en" to """
For good and valuable consideration, the receipt and sufficiency of which is hereby acknowledged, I, {model} ("Model"), grant to {photographer} ("Photographer") and Photographer's licensees, successors, and assigns the irrevocable, perpetual, worldwide right and license to use, reproduce, publish, distribute, display, and create derivative works from photographs and other images of Model captured in connection with the described shoot, in any medium now known or later developed, for any lawful commercial, editorial, promotional, or artistic purpose.

Model represents that Model is of legal age and has full authority to grant the rights set forth herein, or is the parent/guardian of the depicted person and has authority to sign on their behalf. Model releases and discharges Photographer from any and all claims, demands, or causes of action arising out of or related to the use of Model's likeness as authorized by this release, to the fullest extent permitted by applicable law.

Model acknowledges that this document was signed electronically, that the signature below is Model's own, and that the timestamp and location metadata (if available) were captured at the moment of signing for record-keeping purposes.

This release constitutes the entire agreement between the parties concerning the subject matter hereof and may be signed in electronic form. Terms version: {version}.
""",
        "es" to """
Por la presente y a cambio de una contraprestación válida, cuya recepción se reconoce, yo, {model} («Modelo»), otorgo a {photographer} («Fotógrafo») y a sus licenciatarios, sucesores y cesionarios el derecho irrevocable, perpetuo y mundial a usar, reproducir, publicar, distribuir, exhibir y crear obras derivadas de fotografías e imágenes del Modelo captadas en la sesión descrita, en cualquier medio, con fines comerciales, editoriales, promocionales o artísticos lícitos.

El Modelo declara ser mayor de edad y tener plena autoridad para otorgar estos derechos, o ser el padre/tutor de la persona representada. El Modelo libera al Fotógrafo de reclamaciones relacionadas con el uso autorizado de su imagen, en la máxima medida permitida por la ley.

El Modelo reconoce la firma electrónica y que la marca de tiempo y ubicación (si están) se capturaron al firmar. Esta cesión es el acuerdo completo entre las partes. Versión de términos: {version}.
""",
        "fr" to """
Pour une contrepartie valable, dont la réception est reconnue, je, {model} (« Modèle »), accorde à {photographer} (« Photographe ») et à ses licenciés, successeurs et ayants droit le droit irrévocable, perpétuel et mondial d’utiliser, reproduire, publier, distribuer, afficher et créer des œuvres dérivées des photographies et images du Modèle issues de la séance décrite, sur tout support, à des fins commerciales, éditoriales, promotionnelles ou artistiques licites.

Le Modèle déclare être majeur et avoir l’autorité d’accorder ces droits, ou être le parent/tuteur de la personne représentée. Le Modèle décharge le Photographe des réclamations liées à l’usage autorisé de son image, dans la mesure permise par la loi.

Le Modèle reconnaît la signature électronique et que l’horodatage et la localisation (le cas échéant) ont été capturés au moment de la signature. Cette autorisation constitue l’intégralité de l’accord. Version des termes : {version}.
""",
        "it" to """
Per un valido corrispettivo, di cui si riconosce la ricezione, io, {model} («Modello»), concedo a {photographer} («Fotografo») e ai suoi licenziatari, successori e aventi causa il diritto irrevocabile, perpetuo e mondiale di usare, riprodurre, pubblicare, distribuire, esporre e creare opere derivate da fotografie e immagini del Modello realizzate nella sessione descritta, su qualsiasi supporto, per scopi commerciali, editoriali, promozionali o artistici leciti.

Il Modello dichiara di essere maggiorenne e di avere piena autorità a concedere tali diritti, oppure di essere genitore/tutore della persona raffigurata. Il Modello solleva il Fotografo da pretese relative all’uso autorizzato della propria immagine, nella misura massima consentita dalla legge.

Il Modello riconosce la firma elettronica e che data/ora e posizione (se disponibili) sono stati rilevati al momento della firma. Questa liberatoria costituisce l’intero accordo. Versione dei termini: {version}.
""",
        "de" to """
Für eine gültige Gegenleistung, deren Erhalt hiermit bestätigt wird, gewähre ich, {model} („Model“), {photographer} („Fotograf“) sowie dessen Lizenznehmern, Rechtsnachfolgern und Abtretungsempfängern das unwiderrufliche, unbefristete und weltweite Recht, Fotografien und Bilder des Models aus dem beschriebenen Shooting zu nutzen, zu vervielfältigen, zu veröffentlichen, zu verbreiten, auszustellen und abgeleitete Werke zu erstellen – in jedem Medium und für rechtmäßige kommerzielle, redaktionelle, werbliche oder künstlerische Zwecke.

Das Model erklärt, volljährig zu sein und die Rechte erteilen zu dürfen, oder Erziehungsberechtigter der abgebildeten Person zu sein. Das Model stellt den Fotografen von Ansprüchen frei, die aus der autorisierten Nutzung des Abbilds entstehen, soweit gesetzlich zulässig.

Das Model bestätigt die elektronische Unterschrift sowie Zeit- und Ortsmetadaten (falls vorhanden) zum Zeitpunkt der Unterzeichnung. Diese Freigabe ist die vollständige Vereinbarung. Versionsnummer: {version}.
""",
        "fa" to """
به موجب این رضایت‌نامه، {model} («مدل») به عکاس / فیلم‌بردار {photographer} («عکاس / فیلم‌بردار / استودیو») حق مطلق و غیرقابل‌فسخ و اجازهٔ نامحدود استفاده در مورد عکس‌ها / فیلم‌هایی که از ایشان در جلسهٔ توصیف‌شده در این سند (با تاریخ و محل ثبت‌شده در متادیتای امضا یا جزئیات جلسه) گرفته شده است را طبق شرایط زیر اعطا می‌نماید:

۱ـ حق کپی‌رایت عکس‌ها و فیلم‌ها منحصراً به عکاس / فیلم‌بردار / استودیوی {photographer} و وراث قانونی عکاس / فیلم‌بردار / استودیو تعلق دارد.

۲ـ مدل حق هیچ‌گونه ادعا و مطالبه‌ای که ممکن است از یا در ارتباط با استفاده از عکس‌ها / فیلم‌ها به وجود آید را ندارد و کلیهٔ حقوق عکس‌ها / فیلم‌ها را، اعم از مادی و معنوی، به عکاس / فیلم‌بردار / استودیو واگذار می‌نماید.

۳ـ عکاس / فیلم‌بردار / استودیو حق هرگونه استفادهٔ تجاری، تبلیغاتی، نمایش در نمایشگاه‌ها، استفاده در رزومهٔ کاری، جزوات آموزشی، کتاب، نشریات، وب‌سایت شخصی، وبلاگ، شبکه‌های اجتماعی و محیط اینترنت و کلیهٔ موارد چاپی و نمایشی دیگر، مسابقات عکاسی و جشنواره‌های هنری را بدون نیاز به اخذ مجوز مجدد از مدل، با رعایت قوانین کشور جمهوری اسلامی ایران و یا کشوری که در آن از عکس‌ها / فیلم‌ها استفاده می‌شود، دارد.

۴ـ عکاس / فیلم‌بردار / استودیو حق تغییر در قسمت یا تمامی هر یک از عکس‌ها و فیلم‌ها، رتوش و ادیت، مونتاژ و هرگونه دخل و تصرف در عکس‌ها / فیلم‌ها را، با توجه به قوانین کشور جمهوری اسلامی ایران و یا کشوری که در آن از عکس‌ها / فیلم‌ها استفاده می‌شود، دارد.

۵ـ عکس‌ها و فیلم‌ها با رعایت قوانین جمهوری اسلامی ایران از مدل گرفته شده‌اند.

اینجانب {model} کلیهٔ موارد ذکرشده را به‌دقت مطالعه نموده و با درک کامل آن‌ها، این رضایت‌نامه را به‌صورت الکترونیکی امضا می‌نمایم. این سند برای من، وراث قانونی و نمایندگان قانونی من الزام‌آور است. نسخهٔ شرایط: {version}.
""",
    )

    private val five_hundred_px_style_bodies: Map<String, String> = mapOf(
        "en" to """
I, {model} ("Model"), grant {photographer} ("Photographer") permission to display, share, and promote photographs of Model from the described shoot on creative and photography community platforms (including portfolio sites and social networks), and to use such images in Photographer's personal and professional portfolio.

Model understands images may be publicly viewable online and that platform terms of service (of any third-party site) apply separately; this document is not an official form of any platform. Model confirms legal age/authority to sign and releases Photographer from claims arising from authorized online/portfolio use, to the fullest extent permitted by law.

Electronic signature and signing metadata (time/location when available) are acknowledged for record-keeping. Terms version: {version} (unofficial 500px-style template).
""",
        "es" to """
Yo, {model} («Modelo»), autorizo a {photographer} («Fotógrafo») a mostrar, compartir y promocionar fotografías del Modelo de la sesión descrita en plataformas creativas y de fotografía (incluidos sitios de portfolio y redes sociales), y a usarlas en el portfolio personal y profesional del Fotógrafo.

El Modelo entiende que las imágenes pueden ser públicas en línea y que las condiciones de cada plataforma aplican por separado; este documento no es un formulario oficial de ninguna plataforma. Confirma edad/autoridad legal y libera al Fotógrafo de reclamaciones por el uso autorizado en línea/portfolio, en la máxima medida permitida.

Se reconoce la firma electrónica y los metadatos de firma. Versión: {version} (plantilla no oficial estilo 500px).
""",
        "fr" to """
Je, {model} (« Modèle »), autorise {photographer} (« Photographe ») à afficher, partager et promouvoir les photographies du Modèle issues de la séance sur des plateformes créatives et photo (portfolios et réseaux sociaux), et à les utiliser dans le portfolio personnel et professionnel du Photographe.

Le Modèle comprend que les images peuvent être publiques en ligne et que les conditions des plateformes s’appliquent séparément ; ce document n’est pas un formulaire officiel. Confirmation d’âge/autorité et décharge pour l’usage en ligne/portfolio autorisé. Signature électronique et métadonnées reconnues. Version : {version} (modèle non officiel style 500px).
""",
        "it" to """
Io, {model} («Modello»), autorizzo {photographer} («Fotografo») a mostrare, condividere e promuovere le fotografie del Modello della sessione descritta su piattaforme creative e fotografiche (portfolio e social), e a usarle nel portfolio personale e professionale del Fotografo.

Il Modello comprende che le immagini possono essere pubbliche online e che i termini delle piattaforme si applicano separatamente; questo non è un modulo ufficiale. Conferma età/autorità e solleva il Fotografo per l’uso online/portfolio autorizzato. Firma elettronica e metadati riconosciuti. Versione: {version} (modello non ufficiale stile 500px).
""",
        "de" to """
Ich, {model} („Model“), gestatte {photographer} („Fotograf“), Fotos des Models aus dem beschriebenen Shooting auf Kreativ- und Fotografie-Community-Plattformen (Portfolio-Seiten und soziale Netzwerke) zu zeigen, zu teilen und zu bewerben sowie im persönlichen und beruflichen Portfolio zu nutzen.

Das Model versteht, dass Bilder online öffentlich sichtbar sein können und Plattform-AGB gesondert gelten; dies ist kein offizielles Formular einer Plattform. Bestätigung der Volljährigkeit/Befugnis und Freistellung für autorisierte Online-/Portfolio-Nutzung. Elektronische Unterschrift und Metadaten werden anerkannt. Version: {version} (inoffizielle 500px-Stil-Vorlage).
""",
        "fa" to """
به موجب این رضایت‌نامه، {model} («مدل») به عکاس / فیلم‌بردار {photographer} («عکاس / فیلم‌بردار / استودیو») حق و اجازهٔ استفاده از عکس‌ها / فیلم‌هایی که از ایشان در جلسهٔ توصیف‌شده در این سند گرفته شده است را طبق شرایط زیر اعطا می‌نماید:

۱ـ حق کپی‌رایت عکس‌ها و فیلم‌ها منحصراً به عکاس / فیلم‌بردار / استودیوی {photographer} و وراث قانونی ایشان تعلق دارد.

۲ـ مدل حق هیچ‌گونه ادعا و مطالبه‌ای ناشی از استفادهٔ مجاز آنلاین / پورتفولیو از عکس‌ها / فیلم‌ها را ندارد و حقوق مرتبط را به عکاس / فیلم‌بردار / استودیو واگذار می‌نماید.

۳ـ عکاس / فیلم‌بردار / استودیو حق نمایش، اشتراک و ترویج عکس‌ها / فیلم‌ها در پلتفرم‌های خلاقانه و عکاسی (از جمله سایت‌های پورتفولیو و شبکه‌های اجتماعی) و استفاده در پورتفولیؤ شخصی و حرفه‌ای خود را، با رعایت قوانین جمهوری اسلامی ایران و یا کشور محل استفاده، دارد. این سند فرم رسمی هیچ پلتفرم ثالثی نیست و شرایط هر پلتفرم جداگانه اعمال می‌شود.

۴ـ عکاس / فیلم‌بردار / استودیو در حد متعارف حق رتوش، ادیت و آماده‌سازی نمایشی عکس‌ها / فیلم‌ها را دارد.

۵ـ عکس‌ها و فیلم‌ها با رعایت قوانین جمهوری اسلامی ایران از مدل گرفته شده‌اند.

اینجانب {model} موارد بالا را مطالعه و درک کرده و این رضایت‌نامه را به‌صورت الکترونیکی امضا می‌نمایم. نسخهٔ شرایط: {version} (قالب غیررسمی به سبک پلتفرم‌های پورتفولیو).
""",
    )

    private val stock_rf_style_bodies: Map<String, String> = mapOf(
        "en" to """
I, {model} ("Model"), grant {photographer} ("Photographer") and Photographer's licensees a broad, royalty-free, perpetual, worldwide license to use images of Model from the described shoot for commercial purposes, including advertising, packaging, websites, and resale or licensing through stock or content libraries, without further compensation beyond any agreed shoot fee.

Model waives inspection/approval rights for finished uses where permitted by law, confirms age/authority to sign, and releases Photographer and downstream licensees from claims related to authorized commercial use of Model's likeness.

This is a generic RF-style template, not the official form of any stock agency. Electronic signature and metadata stamps are for record-keeping. Terms version: {version}.
""",
        "es" to """
Yo, {model} («Modelo»), otorgo a {photographer} («Fotógrafo») y a sus licenciatarios una licencia amplia, libre de regalías, perpetua y mundial para usar imágenes del Modelo de la sesión con fines comerciales, incluida publicidad, envases, sitios web y reventa o licencia en bancos de imágenes, sin compensación adicional más allá de la tarifa acordada.

El Modelo renuncia, cuando la ley lo permita, a derechos de inspección/aprobación, confirma edad/autoridad y libera al Fotógrafo y licenciatarios por el uso comercial autorizado.

Plantilla genérica estilo RF, no formulario oficial de ninguna agencia. Firma electrónica y metadatos para registro. Versión: {version}.
""",
        "fr" to """
Je, {model} (« Modèle »), accorde à {photographer} (« Photographe ») et à ses licenciés une licence large, libre de redevances, perpétuelle et mondiale pour utiliser les images du Modèle à des fins commerciales (publicité, emballage, sites web, revente ou licence via banques d’images), sans compensation supplémentaire au-delà des honoraires convenus.

Renonciation aux droits d’inspection/approbation lorsque la loi le permet, confirmation d’âge/autorité et décharge pour l’usage commercial autorisé.

Modèle RF générique, non officiel d’une agence. Signature électronique et métadonnées pour archivage. Version : {version}.
""",
        "it" to """
Io, {model} («Modello»), concedo a {photographer} («Fotografo») e ai suoi licenziatari una licenza ampia, royalty-free, perpetua e mondiale per usare le immagini del Modello a fini commerciali (pubblicità, packaging, siti web, rivendita o licenza tramite archivi stock), senza compensi ulteriori oltre al compenso concordato.

Il Modello rinuncia, ove consentito, a diritti di ispezione/approvazione, conferma età/autorità e solleva Fotografo e licenziatari per l’uso commerciale autorizzato.

Modello generico stile RF, non ufficiale di alcuna agenzia. Firma elettronica e metadati per registrazione. Versione: {version}.
""",
        "de" to """
Ich, {model} („Model“), gewähre {photographer} („Fotograf“) und dessen Lizenznehmern eine umfassende, lizenzgebührenfreie, unbefristete und weltweite Lizenz zur kommerziellen Nutzung der Bilder (Werbung, Verpackung, Websites, Weiterverkauf/Lizenzierung über Stock-Bibliotheken) ohne weitere Vergütung außer dem vereinbarten Shooting-Honorar.

Verzicht auf Prüf-/Freigaberechte soweit gesetzlich zulässig, Bestätigung der Volljährigkeit/Befugnis und Freistellung für autorisierte kommerzielle Nutzung.

Generische RF-Vorlage, kein offizielles Agenturformular. Elektronische Unterschrift und Metadaten zur Dokumentation. Version: {version}.
""",
        "fa" to """
به موجب این رضایت‌نامه، {model} («مدل») به عکاس / فیلم‌بردار {photographer} («عکاس / فیلم‌بردار / استودیو») و مجوزگیرندگان ایشان، حق مطلق، بدون حق‌الامتیاز، دائمی و جهانی استفاده از عکس‌ها / فیلم‌های جلسهٔ توصیف‌شده را طبق شرایط زیر اعطا می‌نماید:

۱ـ حق کپی‌رایت و بهره‌برداری تجاری از عکس‌ها / فیلم‌ها به عکاس / فیلم‌بردار / استودیوی {photographer} و مجوزگیرندگان ایشان تعلق دارد.

۲ـ مدل از هرگونه ادعا و مطالبهٔ ناشی از استفادهٔ تجاری مجاز صرف‌نظر می‌کند و حقوق مادی و معنوی مرتبط را واگذار می‌نماید؛ در حد مجاز قانون از حق بازرسی / تأیید استفادهٔ نهایی صرف‌نظر می‌کند.

۳ـ عکاس / فیلم‌بردار / استودیو و مجوزگیرندگان حق استفاده در تبلیغات، بسته‌بندی، وب‌سایت‌ها، فروش مجدد یا مجوزدهی از طریق کتابخانه‌های استوک / محتوا را بدون جبران بیشتر (جز حق‌الزحمهٔ توافق‌شدهٔ جلسه، در صورت وجود) دارند. این یک قالب عمومی سبک RF است و فرم رسمی هیچ آژانس استوکی نیست.

۴ـ عکاس / فیلم‌بردار / استودیو حق ادیت، رتوش و آماده‌سازی فنی عکس‌ها / فیلم‌ها را دارد.

۵ـ عکس‌ها و فیلم‌ها با رعایت قوانین جمهوری اسلامی ایران از مدل گرفته شده‌اند.

اینجانب {model} موارد بالا را مطالعه و درک کرده و این رضایت‌نامه را به‌صورت الکترونیکی امضا می‌نمایم. نسخهٔ شرایط: {version}.
""",
    )

    private val editorial_only_bodies: Map<String, String> = mapOf(
        "en" to """
I, {model} ("Model"), grant {photographer} ("Photographer") the right to use images of Model from the described shoot for editorial, journalistic, documentary, educational, and newsworthy purposes only (including print and online editorial publications). Commercial advertising or endorsement uses are not granted by this template unless separately agreed in writing.

Model confirms age/authority to sign and releases Photographer from claims arising from authorized editorial uses, to the fullest extent permitted by law. Electronic signature and metadata are for record-keeping. Terms version: {version}.
""",
        "es" to """
Yo, {model} («Modelo»), otorgo a {photographer} («Fotógrafo») el derecho a usar imágenes del Modelo de la sesión solo con fines editoriales, periodísticos, documentales, educativos y de interés informativo (incluidas publicaciones impresas y en línea). La publicidad comercial o el endoso no se conceden salvo acuerdo escrito aparte.

Confirma edad/autoridad y libera al Fotógrafo por usos editoriales autorizados. Firma electrónica y metadatos para registro. Versión: {version}.
""",
        "fr" to """
Je, {model} (« Modèle »), accorde à {photographer} (« Photographe ») le droit d’utiliser les images uniquement à des fins éditoriales, journalistiques, documentaires, éducatives et d’actualité (publications imprimées et en ligne). La publicité commerciale ou l’endossement ne sont pas accordés sauf accord écrit séparé.

Confirmation d’âge/autorité et décharge pour usages éditoriaux autorisés. Signature électronique et métadonnées pour archivage. Version : {version}.
""",
        "it" to """
Io, {model} («Modello»), concedo a {photographer} («Fotografo») il diritto di usare le immagini solo per scopi editoriali, giornalistici, documentari, educativi e di attualità (pubblicazioni stampate e online). Pubblicità commerciale o endorsement non sono concessi salvo accordo scritto separato.

Conferma età/autorità e solleva il Fotografo per usi editoriali autorizzati. Firma elettronica e metadati per registrazione. Versione: {version}.
""",
        "de" to """
Ich, {model} („Model“), gestatte {photographer} („Fotograf“) die Nutzung der Bilder ausschließlich für redaktionelle, journalistische, dokumentarische, pädagogische und nachrichtliche Zwecke (Print und Online). Kommerzielle Werbung oder Endorsement sind nicht umfasst, sofern nicht schriftlich anders vereinbart.

Bestätigung der Volljährigkeit/Befugnis und Freistellung für autorisierte redaktionelle Nutzung. Elektronische Unterschrift und Metadaten zur Dokumentation. Version: {version}.
""",
        "fa" to """
به موجب این رضایت‌نامه، {model} («مدل») به عکاس / فیلم‌بردار {photographer} («عکاس / فیلم‌بردار / استودیو») حق استفاده از عکس‌ها / فیلم‌های جلسهٔ توصیف‌شده را فقط برای مقاصد تحریریه‌ای، خبری، مستند، آموزشی و رویدادهای خبری طبق شرایط زیر اعطا می‌نماید:

۱ـ حق کپی‌رایت آثار به عکاس / فیلم‌بردار / استودیوی {photographer} تعلق دارد.

۲ـ مدل از ادعاهای ناشی از استفادهٔ تحریریه‌ای مجاز صرف‌نظر می‌کند.

۳ـ استفادهٔ تجاری تبلیغاتی یا تأییدیهٔ برند مشمول این رضایت‌نامه نیست مگر با توافق کتبی جداگانه. استفاده در نشریات چاپی و آنلاین تحریریه‌ای مجاز است.

۴ـ ادیت متعارف برای انتشار تحریریه‌ای مجاز است.

۵ـ عکس‌ها و فیلم‌ها با رعایت قوانین جمهوری اسلامی ایران از مدل گرفته شده‌اند.

اینجانب {model} موارد بالا را مطالعه و درک کرده و این رضایت‌نامه را به‌صورت الکترونیکی امضا می‌نمایم. نسخهٔ شرایط: {version}.
""",
    )

    private val social_web_bodies: Map<String, String> = mapOf(
        "en" to """
I, {model} ("Model"), grant {photographer} ("Photographer") permission to use images of Model from the described shoot on websites, blogs, and social media channels, and for light promotional use related to Photographer's services (e.g. website gallery, Instagram, booking materials). Broad third-party advertising campaigns and stock resale are outside the scope of this template unless separately agreed.

Model confirms age/authority to sign and releases Photographer from claims arising from authorized web/social promotional uses, to the fullest extent permitted by law. Electronic signature and metadata are for record-keeping. Terms version: {version}.
""",
        "es" to """
Yo, {model} («Modelo»), autorizo a {photographer} («Fotógrafo») a usar imágenes del Modelo en sitios web, blogs y redes sociales, y para promoción ligera de sus servicios (galería web, Instagram, materiales de reserva). Campañas publicitarias amplias de terceros y reventa stock quedan fuera salvo acuerdo aparte.

Confirma edad/autoridad y libera al Fotógrafo por usos web/social autorizados. Firma electrónica y metadatos para registro. Versión: {version}.
""",
        "fr" to """
Je, {model} (« Modèle »), autorise {photographer} (« Photographe ») à utiliser les images sur sites web, blogs et réseaux sociaux, et pour une promotion légère de ses services (galerie, Instagram, supports de réservation). Campagnes publicitaires tierces larges et revente stock hors champ sauf accord séparé.

Confirmation d’âge/autorité et décharge pour usages web/sociaux autorisés. Signature électronique et métadonnées pour archivage. Version : {version}.
""",
        "it" to """
Io, {model} («Modello»), autorizzo {photographer} («Fotografo») a usare le immagini su siti web, blog e social, e per promozione leggera dei suoi servizi (galleria, Instagram, materiali di booking). Campagne pubblicitarie terze ampie e rivendita stock sono escluse salvo accordo separato.

Conferma età/autorità e solleva il Fotografo per usi web/social autorizzati. Firma elettronica e metadati per registrazione. Versione: {version}.
""",
        "de" to """
Ich, {model} („Model“), gestatte {photographer} („Fotograf“), die Bilder auf Websites, Blogs und Social Media sowie für leichte Werbung der eigenen Dienste (Galerie, Instagram, Buchungsmaterial) zu nutzen. Breite Drittwerbung und Stock-Weiterverkauf sind ausgenommen, sofern nicht anders vereinbart.

Bestätigung der Volljährigkeit/Befugnis und Freistellung für autorisierte Web-/Social-Nutzung. Elektronische Unterschrift und Metadaten zur Dokumentation. Version: {version}.
""",
        "fa" to """
به موجب این رضایت‌نامه، {model} («مدل») به عکاس / فیلم‌بردار {photographer} («عکاس / فیلم‌بردار / استودیو») اجازه می‌دهد از عکس‌ها / فیلم‌های جلسهٔ توصیف‌شده در وب‌سایت‌ها، وبلاگ‌ها و شبکه‌های اجتماعی و برای تبلیغ سبک خدمات عکاس (گالری سایت، اینستاگرام، مواد رزرو) طبق شرایط زیر استفاده شود:

۱ـ حق کپی‌رایت به عکاس / فیلم‌بردار / استودیوی {photographer} تعلق دارد.

۲ـ مدل از ادعاهای ناشی از استفادهٔ وب / اجتماعی مجاز صرف‌نظر می‌کند.

۳ـ کمپین‌های تبلیغاتی گستردهٔ شخص ثالث و فروش استوک خارج از این رضایت‌نامه است مگر توافق جداگانه.

۴ـ ادیت و آماده‌سازی متعارف برای انتشار آنلاین مجاز است.

۵ـ عکس‌ها و فیلم‌ها با رعایت قوانین جمهوری اسلامی ایران از مدل گرفته شده‌اند.

اینجانب {model} موارد بالا را مطالعه و درک کرده و این رضایت‌نامه را به‌صورت الکترونیکی امضا می‌نمایم. نسخهٔ شرایط: {version}.
""",
    )

}
