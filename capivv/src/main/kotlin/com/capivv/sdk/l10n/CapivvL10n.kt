package com.capivv.sdk.l10n

/**
 * Localization support for Capivv SDK.
 * Supports 8 languages: English, Spanish, French, German, Japanese, Chinese, Portuguese, Italian.
 */
object CapivvL10n {

    private val translations: Map<String, Map<String, String>> = mapOf(
        "en" to mapOf(
            // Paywall
            "unlock_premium" to "Unlock Premium",
            "subscribe_now" to "Subscribe Now",
            "restore_purchases" to "Restore Purchases",
            "continue" to "Continue",
            "cancel" to "Cancel",
            "close" to "Close",
            "loading" to "Loading...",
            "loading_products" to "Loading products...",
            "retry" to "Retry",
            "error" to "Error",

            // Purchase
            "purchase_successful" to "Purchase Successful",
            "purchase_failed" to "Purchase Failed",
            "purchase_cancelled" to "Purchase Cancelled",
            "purchase_pending" to "Purchase Pending",
            "restore_successful" to "Purchases Restored",
            "restore_failed" to "Restore Failed",
            "no_purchases_to_restore" to "No purchases to restore",

            // Product
            "free_trial" to "Free Trial",
            "trial_period" to "%@ free trial",
            "per_month" to "/ month",
            "per_year" to "/ year",
            "per_week" to "/ week",
            "monthly" to "Monthly",
            "yearly" to "Annual",
            "weekly" to "Weekly",
            "best_value" to "Best Value",
            "most_popular" to "Most Popular",

            // Legal
            "terms_of_service" to "Terms of Service",
            "privacy_policy" to "Privacy Policy",
            "subscription_disclaimer" to "Subscription automatically renews unless cancelled at least 24 hours before the end of the current period.",

            // Features
            "whats_included" to "What's Included",
            "features" to "Features",

            // Countdown
            "offer_expires" to "Offer Expires",
            "offer_expired" to "Offer Expired",
            "days" to "days",
            "hours" to "hours",
            "minutes" to "minutes",
            "seconds" to "seconds",

            // FAQ
            "faq" to "Frequently Asked Questions",

            // Social Proof
            "reviews" to "reviews",
            "downloads" to "downloads"
        ),

        "es" to mapOf(
            "unlock_premium" to "Desbloquear Premium",
            "subscribe_now" to "Suscribirse Ahora",
            "restore_purchases" to "Restaurar Compras",
            "continue" to "Continuar",
            "cancel" to "Cancelar",
            "close" to "Cerrar",
            "loading" to "Cargando...",
            "loading_products" to "Cargando productos...",
            "retry" to "Reintentar",
            "error" to "Error",
            "purchase_successful" to "Compra Exitosa",
            "purchase_failed" to "Compra Fallida",
            "purchase_cancelled" to "Compra Cancelada",
            "purchase_pending" to "Compra Pendiente",
            "restore_successful" to "Compras Restauradas",
            "restore_failed" to "Restauración Fallida",
            "no_purchases_to_restore" to "No hay compras para restaurar",
            "free_trial" to "Prueba Gratuita",
            "trial_period" to "%@ de prueba gratis",
            "per_month" to "/ mes",
            "per_year" to "/ año",
            "per_week" to "/ semana",
            "monthly" to "Mensual",
            "yearly" to "Anual",
            "weekly" to "Semanal",
            "best_value" to "Mejor Valor",
            "most_popular" to "Más Popular",
            "terms_of_service" to "Términos de Servicio",
            "privacy_policy" to "Política de Privacidad",
            "subscription_disclaimer" to "La suscripción se renueva automáticamente a menos que se cancele al menos 24 horas antes del final del período actual.",
            "whats_included" to "Qué Incluye",
            "features" to "Características",
            "offer_expires" to "La Oferta Expira",
            "offer_expired" to "Oferta Expirada",
            "days" to "días",
            "hours" to "horas",
            "minutes" to "minutos",
            "seconds" to "segundos",
            "faq" to "Preguntas Frecuentes",
            "reviews" to "reseñas",
            "downloads" to "descargas"
        ),

        "fr" to mapOf(
            "unlock_premium" to "Débloquer Premium",
            "subscribe_now" to "S'abonner Maintenant",
            "restore_purchases" to "Restaurer les Achats",
            "continue" to "Continuer",
            "cancel" to "Annuler",
            "close" to "Fermer",
            "loading" to "Chargement...",
            "loading_products" to "Chargement des produits...",
            "retry" to "Réessayer",
            "error" to "Erreur",
            "purchase_successful" to "Achat Réussi",
            "purchase_failed" to "Achat Échoué",
            "purchase_cancelled" to "Achat Annulé",
            "purchase_pending" to "Achat en Attente",
            "restore_successful" to "Achats Restaurés",
            "restore_failed" to "Échec de la Restauration",
            "no_purchases_to_restore" to "Aucun achat à restaurer",
            "free_trial" to "Essai Gratuit",
            "trial_period" to "%@ d'essai gratuit",
            "per_month" to "/ mois",
            "per_year" to "/ an",
            "per_week" to "/ semaine",
            "monthly" to "Mensuel",
            "yearly" to "Annuel",
            "weekly" to "Hebdomadaire",
            "best_value" to "Meilleure Offre",
            "most_popular" to "Plus Populaire",
            "terms_of_service" to "Conditions d'Utilisation",
            "privacy_policy" to "Politique de Confidentialité",
            "subscription_disclaimer" to "L'abonnement se renouvelle automatiquement sauf annulation au moins 24 heures avant la fin de la période en cours.",
            "whats_included" to "Ce Qui Est Inclus",
            "features" to "Fonctionnalités",
            "offer_expires" to "L'Offre Expire",
            "offer_expired" to "Offre Expirée",
            "days" to "jours",
            "hours" to "heures",
            "minutes" to "minutes",
            "seconds" to "secondes",
            "faq" to "Questions Fréquentes",
            "reviews" to "avis",
            "downloads" to "téléchargements"
        ),

        "de" to mapOf(
            "unlock_premium" to "Premium Freischalten",
            "subscribe_now" to "Jetzt Abonnieren",
            "restore_purchases" to "Käufe Wiederherstellen",
            "continue" to "Weiter",
            "cancel" to "Abbrechen",
            "close" to "Schließen",
            "loading" to "Laden...",
            "loading_products" to "Produkte werden geladen...",
            "retry" to "Wiederholen",
            "error" to "Fehler",
            "purchase_successful" to "Kauf Erfolgreich",
            "purchase_failed" to "Kauf Fehlgeschlagen",
            "purchase_cancelled" to "Kauf Abgebrochen",
            "purchase_pending" to "Kauf Ausstehend",
            "restore_successful" to "Käufe Wiederhergestellt",
            "restore_failed" to "Wiederherstellung Fehlgeschlagen",
            "no_purchases_to_restore" to "Keine Käufe zum Wiederherstellen",
            "free_trial" to "Kostenlose Testversion",
            "trial_period" to "%@ kostenlos testen",
            "per_month" to "/ Monat",
            "per_year" to "/ Jahr",
            "per_week" to "/ Woche",
            "monthly" to "Monatlich",
            "yearly" to "Jährlich",
            "weekly" to "Wöchentlich",
            "best_value" to "Bester Wert",
            "most_popular" to "Am Beliebtesten",
            "terms_of_service" to "Nutzungsbedingungen",
            "privacy_policy" to "Datenschutzrichtlinie",
            "subscription_disclaimer" to "Das Abonnement verlängert sich automatisch, es sei denn, es wird mindestens 24 Stunden vor Ende des aktuellen Zeitraums gekündigt.",
            "whats_included" to "Was Enthalten Ist",
            "features" to "Funktionen",
            "offer_expires" to "Angebot Endet",
            "offer_expired" to "Angebot Abgelaufen",
            "days" to "Tage",
            "hours" to "Stunden",
            "minutes" to "Minuten",
            "seconds" to "Sekunden",
            "faq" to "Häufig Gestellte Fragen",
            "reviews" to "Bewertungen",
            "downloads" to "Downloads"
        ),

        "ja" to mapOf(
            "unlock_premium" to "プレミアムを解除",
            "subscribe_now" to "今すぐ購読",
            "restore_purchases" to "購入を復元",
            "continue" to "続ける",
            "cancel" to "キャンセル",
            "close" to "閉じる",
            "loading" to "読み込み中...",
            "loading_products" to "商品を読み込み中...",
            "retry" to "再試行",
            "error" to "エラー",
            "purchase_successful" to "購入完了",
            "purchase_failed" to "購入失敗",
            "purchase_cancelled" to "購入キャンセル",
            "purchase_pending" to "購入保留中",
            "restore_successful" to "購入を復元しました",
            "restore_failed" to "復元に失敗しました",
            "no_purchases_to_restore" to "復元する購入がありません",
            "free_trial" to "無料トライアル",
            "trial_period" to "%@ 無料お試し",
            "per_month" to "/ 月",
            "per_year" to "/ 年",
            "per_week" to "/ 週",
            "monthly" to "月額",
            "yearly" to "年額",
            "weekly" to "週額",
            "best_value" to "お得",
            "most_popular" to "人気",
            "terms_of_service" to "利用規約",
            "privacy_policy" to "プライバシーポリシー",
            "subscription_disclaimer" to "現在の期間終了の24時間前までにキャンセルしない限り、自動的に更新されます。",
            "whats_included" to "含まれるもの",
            "features" to "機能",
            "offer_expires" to "オファー終了",
            "offer_expired" to "オファー終了済み",
            "days" to "日",
            "hours" to "時間",
            "minutes" to "分",
            "seconds" to "秒",
            "faq" to "よくある質問",
            "reviews" to "レビュー",
            "downloads" to "ダウンロード"
        ),

        "zh" to mapOf(
            "unlock_premium" to "解锁高级版",
            "subscribe_now" to "立即订阅",
            "restore_purchases" to "恢复购买",
            "continue" to "继续",
            "cancel" to "取消",
            "close" to "关闭",
            "loading" to "加载中...",
            "loading_products" to "正在加载产品...",
            "retry" to "重试",
            "error" to "错误",
            "purchase_successful" to "购买成功",
            "purchase_failed" to "购买失败",
            "purchase_cancelled" to "购买已取消",
            "purchase_pending" to "购买待处理",
            "restore_successful" to "购买已恢复",
            "restore_failed" to "恢复失败",
            "no_purchases_to_restore" to "没有可恢复的购买",
            "free_trial" to "免费试用",
            "trial_period" to "%@ 免费试用",
            "per_month" to "/ 月",
            "per_year" to "/ 年",
            "per_week" to "/ 周",
            "monthly" to "月度",
            "yearly" to "年度",
            "weekly" to "每周",
            "best_value" to "最超值",
            "most_popular" to "最受欢迎",
            "terms_of_service" to "服务条款",
            "privacy_policy" to "隐私政策",
            "subscription_disclaimer" to "除非在当前期限结束前至少24小时取消，否则订阅将自动续订。",
            "whats_included" to "包含内容",
            "features" to "功能",
            "offer_expires" to "优惠结束",
            "offer_expired" to "优惠已过期",
            "days" to "天",
            "hours" to "小时",
            "minutes" to "分钟",
            "seconds" to "秒",
            "faq" to "常见问题",
            "reviews" to "评价",
            "downloads" to "下载"
        ),

        "pt" to mapOf(
            "unlock_premium" to "Desbloquear Premium",
            "subscribe_now" to "Assinar Agora",
            "restore_purchases" to "Restaurar Compras",
            "continue" to "Continuar",
            "cancel" to "Cancelar",
            "close" to "Fechar",
            "loading" to "Carregando...",
            "loading_products" to "Carregando produtos...",
            "retry" to "Tentar Novamente",
            "error" to "Erro",
            "purchase_successful" to "Compra Bem-sucedida",
            "purchase_failed" to "Compra Falhou",
            "purchase_cancelled" to "Compra Cancelada",
            "purchase_pending" to "Compra Pendente",
            "restore_successful" to "Compras Restauradas",
            "restore_failed" to "Falha na Restauração",
            "no_purchases_to_restore" to "Nenhuma compra para restaurar",
            "free_trial" to "Teste Gratuito",
            "trial_period" to "%@ de teste grátis",
            "per_month" to "/ mês",
            "per_year" to "/ ano",
            "per_week" to "/ semana",
            "monthly" to "Mensal",
            "yearly" to "Anual",
            "weekly" to "Semanal",
            "best_value" to "Melhor Valor",
            "most_popular" to "Mais Popular",
            "terms_of_service" to "Termos de Serviço",
            "privacy_policy" to "Política de Privacidade",
            "subscription_disclaimer" to "A assinatura é renovada automaticamente, a menos que seja cancelada pelo menos 24 horas antes do final do período atual.",
            "whats_included" to "O Que Está Incluído",
            "features" to "Recursos",
            "offer_expires" to "Oferta Expira",
            "offer_expired" to "Oferta Expirada",
            "days" to "dias",
            "hours" to "horas",
            "minutes" to "minutos",
            "seconds" to "segundos",
            "faq" to "Perguntas Frequentes",
            "reviews" to "avaliações",
            "downloads" to "downloads"
        ),

        "it" to mapOf(
            "unlock_premium" to "Sblocca Premium",
            "subscribe_now" to "Abbonati Ora",
            "restore_purchases" to "Ripristina Acquisti",
            "continue" to "Continua",
            "cancel" to "Annulla",
            "close" to "Chiudi",
            "loading" to "Caricamento...",
            "loading_products" to "Caricamento prodotti...",
            "retry" to "Riprova",
            "error" to "Errore",
            "purchase_successful" to "Acquisto Completato",
            "purchase_failed" to "Acquisto Fallito",
            "purchase_cancelled" to "Acquisto Annullato",
            "purchase_pending" to "Acquisto in Attesa",
            "restore_successful" to "Acquisti Ripristinati",
            "restore_failed" to "Ripristino Fallito",
            "no_purchases_to_restore" to "Nessun acquisto da ripristinare",
            "free_trial" to "Prova Gratuita",
            "trial_period" to "%@ di prova gratuita",
            "per_month" to "/ mese",
            "per_year" to "/ anno",
            "per_week" to "/ settimana",
            "monthly" to "Mensile",
            "yearly" to "Annuale",
            "weekly" to "Settimanale",
            "best_value" to "Miglior Valore",
            "most_popular" to "Più Popolare",
            "terms_of_service" to "Termini di Servizio",
            "privacy_policy" to "Informativa sulla Privacy",
            "subscription_disclaimer" to "L'abbonamento si rinnova automaticamente a meno che non venga annullato almeno 24 ore prima della fine del periodo corrente.",
            "whats_included" to "Cosa È Incluso",
            "features" to "Funzionalità",
            "offer_expires" to "L'Offerta Scade",
            "offer_expired" to "Offerta Scaduta",
            "days" to "giorni",
            "hours" to "ore",
            "minutes" to "minuti",
            "seconds" to "secondi",
            "faq" to "Domande Frequenti",
            "reviews" to "recensioni",
            "downloads" to "download"
        )
    )

    /**
     * Get a localized string.
     *
     * @param key The string key
     * @param locale The locale code (e.g., "en", "es")
     * @return The localized string, or the English fallback, or the key itself
     */
    fun get(key: String, locale: String = "en"): String {
        val languageCode = locale.split("-").first().lowercase()
        return translations[languageCode]?.get(key)
            ?: translations["en"]?.get(key)
            ?: key
    }

    /**
     * Get a localized string with format arguments.
     *
     * @param key The string key
     * @param locale The locale code
     * @param args Format arguments
     * @return The formatted localized string
     */
    fun format(key: String, locale: String = "en", vararg args: Any): String {
        var result = get(key, locale)
        args.forEach { arg ->
            result = result.replaceFirst("%@", arg.toString())
        }
        return result
    }

    /**
     * Get all supported locale codes.
     */
    val supportedLocales: Set<String>
        get() = translations.keys
}
