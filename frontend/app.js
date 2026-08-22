/**
 * ============================================================
 * ITO — Intelligent Traffic Optima
 * Ministry of Railways (CRIS / Indian Railways)
 * 
 * Production Frontend Controller
 * Pure Vanilla JS — Zero Framework Dependencies
 * ============================================================
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ══════════════════════════════════════════════
       1. i18n — Multi-Language Indian Localization
       ══════════════════════════════════════════════ */

    const i18n = {
        en: {
            appName: 'Intelligent Traffic Optima',
            ministry: 'Ministry of Railways',
            navDashboard: 'Dashboard',
            navSchedules: 'Schedules',
            navNetworkMap: 'Network Map',
            navReports: 'Reports',
            whatIfTitle: 'What-If Simulation',
            scenarioLabel: 'Scenario',
            scenarioFreight: 'Freight Delay (Node A)',
            scenarioMaintenance: 'Track Maintenance',
            scenarioWeather: 'Weather Alert',
            autoResolveLabel: 'Auto-Resolve Conflicts',
            runSimulation: 'Run Simulation',
            dominoAlerts: 'Domino Delay Alerts',
            alert1: 'Vande Bharat 20643 approaching Track Sector 4 at Dindigul Junction (DG)',
            alert2: 'Freight BOXN-77 approaching capacity at Karur Junction (KRR)',
            conflictCountdown: '⚠ Unattended Conflict — Auto-Escalation In:',
            systemStatus: 'System Status',
            statusOptimal: 'Optimal',
            statusConflict: 'CONFLICT',
            activeTrains: 'Active Trains',
            networkLoad: 'Network Load',
            networkDelay: 'Est. Network Delay',
            stringChartTitle: 'Time-Distance String Chart',
            btnExport: 'Export',
            btnFilter: 'Filter',
            btnAddRoute: '+ Add Route',
            runningSimulation: 'Running Simulation...',
            timetableTitle: 'Train Timetable & Live Schedules',
            thTrainNumber: 'Train Number',
            thTrainName: 'Train Name',
            thType: 'Type',
            thCurrentStation: 'Current Station',
            thScheduledArr: 'Scheduled Arrival',
            thExpectedArr: 'Expected Arrival',
            thStatus: 'Status',
            statusOnTime: 'ON TIME',
            trackSchematicTitle: 'Track Schematic Diagram',
            btnZoomIn: 'Zoom In',
            btnZoomOut: 'Zoom Out',
            reportsTitle: 'Bottleneck & Conflict Analytics',
            btnDownloadPDF: 'Download PDF',
            btnShare: 'Share',
            impactTitle: 'National Network Impact Metrics',
            impactSubtitle: 'Live summary of optimization performance across all zones',
            fuelSaved: 'Total Fuel Saved',
            delaysPrevented: 'Cascading Delays Prevented',
            networkThroughput: 'Network Throughput',
            badgeCritical: 'Critical',
            badgeWarning: 'Warning',
            badgeOptimized: 'Optimized',
            card1Title: 'Dindigul Junction (DG) Track Sector 4 Bottleneck',
            card1Desc: 'Vande Bharat 20643 signal conflict resolved by prioritizing the Karur loop line siding.',
            card2Title: 'Karur Junction (KRR) Loop Line Capacity',
            card2Desc: 'Active loop line siding at Karur Junction is staging Tuticorin coal and cement freight BOXN-77.',
            card3Title: 'Erode to Tirunelveli Corridor Optimization',
            card3Desc: 'The resolver coordinates Pandian Superfast Express 12637 with the freight crossover movement.',
            cascadingDelayLabel: 'Cascading Delay Projection (Mins)',
            resolutionProgress: 'Resolution Progress',
            loopOccupancyLabel: 'Loop Line Occupancy Trend',
            occupancyLevel: 'Occupancy Level',
            throughputLabel: 'Throughput Efficiency (%)',
            efficiencyIndex: 'Efficiency Index',
            metricDelaysPrevented: 'Delays Prevented',
            metricEstSavings: 'Estimated Savings',
            metricWaitTime: 'Wait Time Projection',
            metricRiskIndex: 'Risk Index',
            elevated: 'Elevated',
            metricFuelSaved: 'Fuel Saved',
            metricThroughputGain: 'Throughput gain',
            auditTrailTitle: '🔒 Escalation Audit Trail',
            auditTime: 'Timestamp',
            auditEvent: 'Event',
            auditAction: 'Action Taken',
            auditOperator: 'Operator',
            auditResult: 'Result',
            filterModalTitle: 'Filter Trains',
            filterByType: 'By Type',
            filterByStatus: 'By Status',
            btnReset: 'Reset',
            btnApply: 'Apply Filters',
            addRouteTitle: 'Add New Route',
            newTrainNumber: 'Train Number',
            newTrainName: 'Train Name',
            newTrainType: 'Type',
            newOrigin: 'Origin Station',
            newDestination: 'Destination Station',
            btnCancel: 'Cancel',
            escalationTitle: '⚠️ UNATTENDED TRAIN CONFLICT DETECTED AT DINDIGUL JUNCTION (DG) (TRACK SECTOR 4)',
            escalationSubtitle: 'Critical conflict has remained unresolved for 15 seconds. Autonomous safety protocol initiated.',
            broadcastLabel: '📡 Emergency Broadcast Status:',
            broadcastMsg: 'Emergency Alert Transmitted to Dindigul Station Master, Field S&T Dispatcher, and Guard Rake BOXN-77 via Proximity Rail-Band.',
            escalationTimerLabel: 'Autonomous override available. Select action:',
        },
        hi: {
            appName: 'इंटेलिजेंट ट्रैफिक ऑप्टिमा',
            ministry: 'रेल मंत्रालय',
            navDashboard: 'डैशबोर्ड',
            navSchedules: 'अनुसूचियाँ',
            navNetworkMap: 'नेटवर्क मानचित्र',
            navReports: 'रिपोर्ट',
            whatIfTitle: 'What-If सिमुलेशन',
            scenarioLabel: 'परिदृश्य',
            scenarioFreight: 'मालगाड़ी विलंब (नोड A)',
            scenarioMaintenance: 'ट्रैक रखरखाव',
            scenarioWeather: 'मौसम चेतावनी',
            autoResolveLabel: 'स्वतः-विवाद समाधान',
            runSimulation: 'सिमुलेशन चलाएँ',
            dominoAlerts: 'डोमिनो विलंब अलर्ट',
            alert1: 'एक्सप्रेस 12041 विलंब सेक्टर 4 तक फैल रहा है',
            alert2: 'मालगाड़ी 9022 क्षमता तक पहुँच रही है',
            conflictCountdown: '⚠ अनुपस्थित विवाद — स्वतः-वृद्धि समय:',
            systemStatus: 'सिस्टम स्थिति',
            statusOptimal: 'सर्वोत्तम',
            statusConflict: 'विवाद',
            activeTrains: 'सक्रिय ट्रेनें',
            networkLoad: 'नेटवर्क लोड',
            networkDelay: 'अनु. नेटवर्क विलंब',
            stringChartTitle: 'समय-दूरी स्ट्रिंग चार्ट',
            btnExport: 'निर्यात',
            btnFilter: 'फ़िल्टर',
            btnAddRoute: '+ मार्ग जोड़ें',
            runningSimulation: 'सिमुलेशन चल रहा है...',
            timetableTitle: 'ट्रेन समय सारणी और लाइव अनुसूची',
            thTrainNumber: 'ट्रेन संख्या',
            thTrainName: 'ट्रेन का नाम',
            thType: 'प्रकार',
            thCurrentStation: 'वर्तमान स्टेशन',
            thScheduledArr: 'निर्धारित आगमन',
            thExpectedArr: 'अपेक्षित आगमन',
            thStatus: 'स्थिति',
            statusOnTime: 'समय पर',
            trackSchematicTitle: 'ट्रैक योजनाबद्ध आरेख',
            btnZoomIn: 'ज़ूम इन',
            btnZoomOut: 'ज़ूम आउट',
            reportsTitle: 'बाधा एवं विवाद विश्लेषण',
            btnDownloadPDF: 'PDF डाउनलोड',
            btnShare: 'साझा करें',
            impactTitle: 'राष्ट्रीय नेटवर्क प्रभाव मेट्रिक्स',
            impactSubtitle: 'सभी ज़ोन पर अनुकूलन प्रदर्शन का लाइव सारांश',
            fuelSaved: 'कुल ईंधन बचत',
            delaysPrevented: 'रोके गए कैस्केडिंग विलंब',
            networkThroughput: 'नेटवर्क थ्रूपुट',
            badgeCritical: 'गंभीर',
            badgeWarning: 'चेतावनी',
            badgeOptimized: 'अनुकूलित',
            card1Title: 'स्टेशन C सेक्टर 4 बाधा',
            card1Desc: 'एक्सप्रेस 20643 (वंदे भारत) सिग्नल विवाद स्वचालित साइडिंग प्राथमिकता द्वारा हल किया गया।',
            card2Title: 'स्टेशन B लूप लाइन क्षमता',
            card2Desc: 'स्टेशन B पर मालगाड़ी लूप लाइन क्षमता के करीब। कोयला मालगाड़ी BOXN-77 क्रॉसओवर 2B पर कतार बना रही है।',
            card3Title: 'सेक्टर 1 अनुसूची अनुकूलन',
            card3Desc: 'स्वचालित विवाद समाधानकर्ता ने कर्नाटक एक्सप्रेस (12627) पुनर्निर्धारित की।',
            cascadingDelayLabel: 'कैस्केडिंग विलंब प्रक्षेपण (मिनट)',
            resolutionProgress: 'समाधान प्रगति',
            loopOccupancyLabel: 'लूप लाइन अधिभोग प्रवृत्ति',
            occupancyLevel: 'अधिभोग स्तर',
            throughputLabel: 'थ्रूपुट दक्षता (%)',
            efficiencyIndex: 'दक्षता सूचकांक',
            metricDelaysPrevented: 'रोके गए विलंब',
            metricEstSavings: 'अनुमानित बचत',
            metricWaitTime: 'प्रतीक्षा समय प्रक्षेपण',
            metricRiskIndex: 'जोखिम सूचकांक',
            elevated: 'बढ़ा हुआ',
            metricFuelSaved: 'ईंधन बचत',
            metricThroughputGain: 'थ्रूपुट लाभ',
            auditTrailTitle: '🔒 वृद्धि ऑडिट ट्रेल',
            auditTime: 'समय',
            auditEvent: 'घटना',
            auditAction: 'की गई कार्रवाई',
            auditOperator: 'ऑपरेटर',
            auditResult: 'परिणाम',
            filterModalTitle: 'ट्रेनें फ़िल्टर करें',
            filterByType: 'प्रकार अनुसार',
            filterByStatus: 'स्थिति अनुसार',
            btnReset: 'रीसेट',
            btnApply: 'फ़िल्टर लागू करें',
            addRouteTitle: 'नया मार्ग जोड़ें',
            newTrainNumber: 'ट्रेन संख्या',
            newTrainName: 'ट्रेन का नाम',
            newTrainType: 'प्रकार',
            newOrigin: 'उद्गम स्टेशन',
            newDestination: 'गंतव्य स्टेशन',
            btnCancel: 'रद्द करें',
            escalationTitle: '⚠️ स्टेशन C (ब्लॉक सेक्टर 4) पर अनुपस्थित ट्रेन विवाद पाया गया',
            escalationSubtitle: 'गंभीर विवाद 15 सेकंड से अनसुलझा है। स्वायत्त सुरक्षा प्रोटोकॉल शुरू किया गया।',
            broadcastLabel: '📡 आपातकालीन प्रसारण स्थिति:',
            broadcastMsg: 'आपातकालीन अलर्ट स्टेशन मास्टर (स्टेशन C), फील्ड S&T डिस्पैचर, और गार्ड रेक BOXN-77 को प्रॉक्सिमिटी रेल-बैंड द्वारा प्रेषित।',
            escalationTimerLabel: 'स्वायत्त ओवरराइड उपलब्ध। कार्रवाई चुनें:',
        },
        ta: {
            appName: 'நுண்ணறிவு போக்குவரத்து உகப்பி',
            ministry: 'இரயில்வே அமைச்சகம்',
            navDashboard: 'டாஷ்போர்ட்',
            navSchedules: 'அட்டவணைகள்',
            navNetworkMap: 'நெட்வொர்க் வரைபடம்',
            navReports: 'அறிக்கைகள்',
            whatIfTitle: 'What-If உருவகப்படுத்துதல்',
            scenarioLabel: 'சூழ்நிலை',
            scenarioFreight: 'சரக்கு தாமதம் (நோட் A)',
            scenarioMaintenance: 'பாதை பராமரிப்பு',
            scenarioWeather: 'வானிலை எச்சரிக்கை',
            autoResolveLabel: 'தானியங்கி தீர்வு',
            runSimulation: 'உருவகப்படுத்து',
            dominoAlerts: 'டோமினோ தாமத எச்சரிக்கைகள்',
            alert1: 'எக்ஸ்பிரஸ் 12041 தாமதம் செக்டர் 4 வரை பரவுகிறது',
            alert2: 'சரக்கு 9022 திறன் எல்லையை நெருங்குகிறது',
            conflictCountdown: '⚠ கவனிக்கப்படாத மோதல் — தானியங்கி விரிவாக்கம்:',
            systemStatus: 'கணினி நிலை',
            statusOptimal: 'சிறந்தது',
            statusConflict: 'மோதல்',
            activeTrains: 'செயலில் உள்ள ரயில்கள்',
            networkLoad: 'நெட்வொர்க் சுமை',
            networkDelay: 'மதிப்பிடப்பட்ட தாமதம்',
            stringChartTitle: 'நேரம்-தூரம் சரம் வரைபடம்',
            btnExport: 'ஏற்றுமதி',
            btnFilter: 'வடிகட்டி',
            btnAddRoute: '+ பாதை சேர்',
            runningSimulation: 'உருவகப்படுத்துதல் நடக்கிறது...',
            timetableTitle: 'ரயில் அட்டவணை & நேரடி நிலை',
            thTrainNumber: 'ரயில் எண்',
            thTrainName: 'ரயில் பெயர்',
            thType: 'வகை',
            thCurrentStation: 'தற்போதைய நிலையம்',
            thScheduledArr: 'திட்டமிட்ட வருகை',
            thExpectedArr: 'எதிர்பார்க்கப்படும் வருகை',
            thStatus: 'நிலை',
            statusOnTime: 'சரியான நேரம்',
            trackSchematicTitle: 'பாதை வரைபட திட்டம்',
            btnZoomIn: 'பெரிதாக்கு',
            btnZoomOut: 'சிறிதாக்கு',
            reportsTitle: 'தடை & மோதல் பகுப்பாய்வு',
            btnDownloadPDF: 'PDF பதிவிறக்கம்',
            btnShare: 'பகிர்',
            impactTitle: 'தேசிய நெட்வொர்க் தாக்க அளவீடுகள்',
            impactSubtitle: 'அனைத்து மண்டலங்களின் உகப்பாக்க செயல்திறன் சுருக்கம்',
            fuelSaved: 'மொத்த எரிபொருள் சேமிப்பு',
            delaysPrevented: 'தடுக்கப்பட்ட தாமதங்கள்',
            networkThroughput: 'நெட்வொர்க் செயல்திறன்',
            badgeCritical: 'ஆபத்தான',
            badgeWarning: 'எச்சரிக்கை',
            badgeOptimized: 'உகப்பாக்கப்பட்டது',
            card1Title: 'நிலையம் C செக்டர் 4 தடை',
            card1Desc: 'எக்ஸ்பிரஸ் 20643 சிக்னல் மோதல் தானியங்கி சைடிங் முன்னுரிமையால் தீர்க்கப்பட்டது.',
            card2Title: 'நிலையம் B லூப் லைன் திறன்',
            card2Desc: 'நிலையம் B யில் சரக்கு லூப் லைன் திறன் எல்லையை நெருங்குகிறது.',
            card3Title: 'செக்டர் 1 அட்டவணை உகப்பாக்கம்',
            card3Desc: 'தானியங்கி மோதல் தீர்வாளர் கர்நாடகா எக்ஸ்பிரஸை மறுதிட்டமிட்டது.',
            cascadingDelayLabel: 'தாமத கூட்டு முன்கணிப்பு (நிமிடங்கள்)',
            resolutionProgress: 'தீர்வு முன்னேற்றம்',
            loopOccupancyLabel: 'லூப் லைன் ஆக்கிரமிப்பு போக்கு',
            occupancyLevel: 'ஆக்கிரமிப்பு நிலை',
            throughputLabel: 'செயல்திறன் திறன் (%)',
            efficiencyIndex: 'திறன் குறியீடு',
            metricDelaysPrevented: 'தடுக்கப்பட்ட தாமதங்கள்',
            metricEstSavings: 'மதிப்பிடப்பட்ட சேமிப்பு',
            metricWaitTime: 'காத்திருப்பு நேர முன்கணிப்பு',
            metricRiskIndex: 'ஆபத்து குறியீடு',
            elevated: 'உயர்வு',
            metricFuelSaved: 'எரிபொருள் சேமிப்பு',
            metricThroughputGain: 'செயல்திறன் லாபம்',
            auditTrailTitle: '🔒 விரிவாக்க தணிக்கை பாதை',
            auditTime: 'நேரம்',
            auditEvent: 'நிகழ்வு',
            auditAction: 'எடுத்த நடவடிக்கை',
            auditOperator: 'ஆபரேட்டர்',
            auditResult: 'முடிவு',
            filterModalTitle: 'ரயில்களை வடிகட்டு',
            filterByType: 'வகை அடிப்படையில்',
            filterByStatus: 'நிலை அடிப்படையில்',
            btnReset: 'மீட்டமை',
            btnApply: 'வடிகட்டிகளை பயன்படுத்து',
            addRouteTitle: 'புதிய பாதை சேர்',
            newTrainNumber: 'ரயில் எண்',
            newTrainName: 'ரயில் பெயர்',
            newTrainType: 'வகை',
            newOrigin: 'தொடக்க நிலையம்',
            newDestination: 'இலக்கு நிலையம்',
            btnCancel: 'ரத்து செய்',
            escalationTitle: '⚠️ நிலையம் C (பிளாக் செக்டர் 4) இல் கவனிக்கப்படாத ரயில் மோதல்',
            escalationSubtitle: '15 வினாடிகளாக தீர்க்கப்படாத மோதல். தானியங்கி பாதுகாப்பு நெறிமுறை தொடங்கப்பட்டது.',
            broadcastLabel: '📡 அவசர ஒலிபரப்பு நிலை:',
            broadcastMsg: 'அவசர எச்சரிக்கை நிலையம் C நிலைய மேலாளர், S&T அனுப்புநர் மற்றும் காவலர் ரேக் BOXN-77 க்கு அனுப்பப்பட்டது.',
            escalationTimerLabel: 'தானியங்கி மேலெழுதல் கிடைக்கிறது. நடவடிக்கையைத் தேர்ந்தெடுக்கவும்:',
        },
        te: {
            appName: 'ఇంటెలిజెంట్ ట్రాఫిక్ ఆప్టిమా', ministry: 'రైల్వే మంత్రిత్వ శాఖ',
            navDashboard: 'డాష్‌బోర్డ్', navSchedules: 'షెడ్యూల్స్', navNetworkMap: 'నెట్‌వర్క్ మ్యాప్', navReports: 'నివేదికలు',
            whatIfTitle: 'What-If సిమ్యులేషన్', scenarioLabel: 'దృశ్యం', scenarioFreight: 'సరుకు ఆలస్యం (నోడ్ A)',
            scenarioMaintenance: 'ట్రాక్ నిర్వహణ', scenarioWeather: 'వాతావరణ హెచ్చరిక',
            autoResolveLabel: 'ఆటో-పరిష్కారం', runSimulation: 'సిమ్యులేషన్ అమలు',
            dominoAlerts: 'డోమినో ఆలస్య అలర్ట్‌లు', alert1: 'ఎక్స్‌ప్రెస్ 12041 ఆలస్యం సెక్టార్ 4కు విస్తరిస్తోంది',
            alert2: 'సరుకు 9022 సామర్థ్యానికి చేరుకుంటోంది', conflictCountdown: '⚠ అసమ్మతి — ఆటో-ఎస్కలేషన్:',
            systemStatus: 'సిస్టమ్ స్థితి', statusOptimal: 'అనుకూలం', statusConflict: 'సంఘర్షణ',
            activeTrains: 'యాక్టివ్ రైళ్లు', networkLoad: 'నెట్‌వర్క్ లోడ్', networkDelay: 'అంచనా ఆలస్యం',
            stringChartTitle: 'సమయ-దూరం స్ట్రింగ్ చార్ట్', btnExport: 'ఎగుమతి', btnFilter: 'ఫిల్టర్',
            btnAddRoute: '+ మార్గం చేర్చు', runningSimulation: 'సిమ్యులేషన్ నడుస్తోంది...',
            timetableTitle: 'రైలు టైమ్‌టేబుల్ & లైవ్ షెడ్యూల్స్',
            thTrainNumber: 'రైలు సంఖ్య', thTrainName: 'రైలు పేరు', thType: 'రకం', thCurrentStation: 'ప్రస్తుత స్టేషన్',
            thScheduledArr: 'షెడ్యూల్డ్ రాక', thExpectedArr: 'అంచనా రాక', thStatus: 'స్థితి', statusOnTime: 'సమయానికి',
            trackSchematicTitle: 'ట్రాక్ స్కీమాటిక్ రేఖాచిత్రం', btnZoomIn: 'జూమ్ ఇన్', btnZoomOut: 'జూమ్ అవుట్',
            reportsTitle: 'అడ్డంకి & సంఘర్షణ విశ్లేషణ', btnDownloadPDF: 'PDF డౌన్‌లోడ్', btnShare: 'షేర్',
            impactTitle: 'జాతీయ నెట్‌వర్క్ ప్రభావ మెట్రిక్స్', impactSubtitle: 'అన్ని జోన్లలో ఆప్టిమైజేషన్ పనితీరు సారాంశం',
            fuelSaved: 'మొత్తం ఇంధనం ఆదా', delaysPrevented: 'నివారించిన ఆలస్యాలు', networkThroughput: 'నెట్‌వర్క్ థ్రూపుట్',
            badgeCritical: 'క్లిష్టమైన', badgeWarning: 'హెచ్చరిక', badgeOptimized: 'ఆప్టిమైజ్ చేయబడింది',
            card1Title: 'స్టేషన్ C సెక్టార్ 4 అడ్డంకి', card1Desc: 'ఎక్స్‌ప్రెస్ 20643 సిగ్నల్ సంఘర్షణ ఆటో సైడింగ్ ద్వారా పరిష్కరించబడింది.',
            card2Title: 'స్టేషన్ B లూప్ లైన్ సామర్థ్యం', card2Desc: 'స్టేషన్ B వద్ద సరుకు లూప్ లైన్ సామర్థ్యానికి చేరుకుంటోంది.',
            card3Title: 'సెక్టార్ 1 షెడ్యూల్ ఆప్టిమైజేషన్', card3Desc: 'ఆటో రిజల్వర్ కర్ణాటక ఎక్స్‌ప్రెస్‌ను రీషెడ్యూల్ చేసింది.',
            cascadingDelayLabel: 'క్యాస్కేడింగ్ ఆలస్య ప్రొజెక్షన్ (నిమిషాలు)', resolutionProgress: 'పరిష్కార ప్రగతి',
            loopOccupancyLabel: 'లూప్ లైన్ ఆక్యుపెన్సీ ట్రెండ్', occupancyLevel: 'ఆక్యుపెన్సీ లెవెల్',
            throughputLabel: 'థ్రూపుట్ ఎఫిషియెన్సీ (%)', efficiencyIndex: 'ఎఫిషియెన్సీ ఇండెక్స్',
            metricDelaysPrevented: 'నివారించిన ఆలస్యాలు', metricEstSavings: 'అంచనా ఆదా',
            metricWaitTime: 'వేచి ఉండే సమయం', metricRiskIndex: 'రిస్క్ ఇండెక్స్', elevated: 'ఎలివేటెడ్',
            metricFuelSaved: 'ఇంధనం ఆదా', metricThroughputGain: 'థ్రూపుట్ లాభం',
            auditTrailTitle: '🔒 ఎస్కలేషన్ ఆడిట్ ట్రెయిల్', auditTime: 'సమయం', auditEvent: 'సంఘటన',
            auditAction: 'తీసుకున్న చర్య', auditOperator: 'ఆపరేటర్', auditResult: 'ఫలితం',
            filterModalTitle: 'రైళ్లను ఫిల్టర్ చేయండి', filterByType: 'రకం ప్రకారం', filterByStatus: 'స్థితి ప్రకారం',
            btnReset: 'రీసెట్', btnApply: 'ఫిల్టర్లు వర్తింపజేయి', addRouteTitle: 'కొత్త మార్గం చేర్చు',
            newTrainNumber: 'రైలు సంఖ్య', newTrainName: 'రైలు పేరు', newTrainType: 'రకం',
            newOrigin: 'మూల స్టేషన్', newDestination: 'గమ్యం స్టేషన్', btnCancel: 'రద్దు చేయి',
            escalationTitle: '⚠️ స్టేషన్ C (బ్లాక్ సెక్టార్ 4) వద్ద అటెండ్ చేయని రైలు సంఘర్షణ',
            escalationSubtitle: '15 సెకన్లుగా పరిష్కరించని సంఘర్షణ. ఆటోనమస్ సేఫ్టీ ప్రోటోకాల్ ప్రారంభించబడింది.',
            broadcastLabel: '📡 అత్యవసర ప్రసార స్థితి:', broadcastMsg: 'స్టేషన్ మాస్టర్, S&T డిస్పాచర్ మరియు గార్డ్ రేక్ BOXN-77కు అలర్ట్ పంపబడింది.',
            escalationTimerLabel: 'ఆటోనమస్ ఓవర్‌రైడ్ అందుబాటులో ఉంది. చర్య ఎంచుకోండి:',
        },
        bn: {
            appName: 'ইন্টেলিজেন্ট ট্রাফিক অপটিমা', ministry: 'রেলওয়ে মন্ত্রণালয়',
            navDashboard: 'ড্যাশবোর্ড', navSchedules: 'সময়সূচী', navNetworkMap: 'নেটওয়ার্ক ম্যাপ', navReports: 'প্রতিবেদন',
            whatIfTitle: 'What-If সিমুলেশন', scenarioLabel: 'পরিস্থিতি', scenarioFreight: 'মালবাহী বিলম্ব (নোড A)',
            scenarioMaintenance: 'ট্র্যাক রক্ষণাবেক্ষণ', scenarioWeather: 'আবহাওয়া সতর্কতা',
            autoResolveLabel: 'স্বয়ংক্রিয় সমাধান', runSimulation: 'সিমুলেশন চালান',
            dominoAlerts: 'ডমিনো বিলম্ব সতর্কতা', systemStatus: 'সিস্টেম স্থিতি', statusOptimal: 'সর্বোত্তম', statusConflict: 'সংঘর্ষ',
            activeTrains: 'সক্রিয় ট্রেন', networkLoad: 'নেটওয়ার্ক লোড', networkDelay: 'আনুমানিক বিলম্ব',
            stringChartTitle: 'সময়-দূরত্ব স্ট্রিং চার্ট', btnExport: 'রপ্তানি', btnFilter: 'ফিল্টার', btnAddRoute: '+ রুট যোগ',
            runningSimulation: 'সিমুলেশন চলছে...', timetableTitle: 'ট্রেন সময়সূচী ও লাইভ',
            thTrainNumber: 'ট্রেন নম্বর', thTrainName: 'ট্রেন নাম', thType: 'ধরন', thCurrentStation: 'বর্তমান স্টেশন',
            thScheduledArr: 'নির্ধারিত আগমন', thExpectedArr: 'প্রত্যাশিত আগমন', thStatus: 'স্থিতি', statusOnTime: 'সময়মত',
            trackSchematicTitle: 'ট্র্যাক স্কিম্যাটিক', btnZoomIn: 'জুম ইন', btnZoomOut: 'জুম আউট',
            reportsTitle: 'বাধা ও সংঘর্ষ বিশ্লেষণ', btnDownloadPDF: 'PDF ডাউনলোড', btnShare: 'শেয়ার',
            impactTitle: 'জাতীয় নেটওয়ার্ক প্রভাব পরিমাপ', impactSubtitle: 'সকল জোনের অপ্টিমাইজেশন কর্মক্ষমতার সারসংক্ষেপ',
            fuelSaved: 'মোট জ্বালানি সঞ্চয়', delaysPrevented: 'প্রতিরোধকৃত বিলম্ব', networkThroughput: 'নেটওয়ার্ক থ্রুপুট',
            badgeCritical: 'জটিল', badgeWarning: 'সতর্কতা', badgeOptimized: 'অপ্টিমাইজড',
            card1Title: 'স্টেশন C সেক্টর 4 বাধা', card2Title: 'স্টেশন B লুপ লাইন ক্ষমতা', card3Title: 'সেক্টর 1 সূচী অপ্টিমাইজেশন',
            card1Desc: 'এক্সপ্রেস 20643 সিগন্যাল সংঘর্ষ স্বয়ংক্রিয় সাইডিংয়ে সমাধান হয়েছে।',
            card2Desc: 'স্টেশন B তে মালবাহী লুপ লাইন ক্ষমতার কাছে।', card3Desc: 'কর্ণাটক এক্সপ্রেস স্বয়ংক্রিয়ভাবে পুনঃনির্ধারিত।',
            cascadingDelayLabel: 'ক্যাসকেডিং বিলম্ব অভিক্ষেপ', resolutionProgress: 'সমাধান অগ্রগতি',
            loopOccupancyLabel: 'লুপ লাইন দখল প্রবণতা', occupancyLevel: 'দখল স্তর',
            throughputLabel: 'থ্রুপুট দক্ষতা (%)', efficiencyIndex: 'দক্ষতা সূচক',
            metricDelaysPrevented: 'প্রতিরোধকৃত বিলম্ব', metricEstSavings: 'আনুমানিক সঞ্চয়',
            metricWaitTime: 'অপেক্ষার সময়', metricRiskIndex: 'ঝুঁকি সূচক', elevated: 'উন্নত',
            metricFuelSaved: 'জ্বালানি সঞ্চয়', metricThroughputGain: 'থ্রুপুট লাভ',
            auditTrailTitle: '🔒 এস্কেলেশন অডিট ট্রেইল', auditTime: 'সময়', auditEvent: 'ঘটনা',
            auditAction: 'গৃহীত ব্যবস্থা', auditOperator: 'অপারেটর', auditResult: 'ফলাফল',
            filterModalTitle: 'ট্রেন ফিল্টার', filterByType: 'ধরন অনুসারে', filterByStatus: 'স্থিতি অনুসারে',
            btnReset: 'রিসেট', btnApply: 'ফিল্টার প্রয়োগ', addRouteTitle: 'নতুন রুট যোগ',
            newTrainNumber: 'ট্রেন নম্বর', newTrainName: 'ট্রেন নাম', newTrainType: 'ধরন',
            newOrigin: 'উৎস স্টেশন', newDestination: 'গন্তব্য স্টেশন', btnCancel: 'বাতিল',
            escalationTitle: '⚠️ স্টেশন C (ব্লক সেক্টর 4) এ অপ্রত্যাশিত ট্রেন সংঘর্ষ',
            escalationSubtitle: '15 সেকেন্ড ধরে অমীমাংসিত সংঘর্ষ। স্বায়ত্তশাসিত নিরাপত্তা প্রোটোকল চালু।',
            broadcastLabel: '📡 জরুরি সম্প্রচার:', broadcastMsg: 'স্টেশন মাস্টার ও S&T ডিসপ্যাচারকে সতর্কতা প্রেরিত।',
            escalationTimerLabel: 'স্বায়ত্তশাসিত ওভাররাইড উপলব্ধ। ব্যবস্থা নির্বাচন করুন:',
            alert1: 'এক্সপ্রেস 12041 বিলম্ব সেক্টর 4 পর্যন্ত ছড়াচ্ছে', alert2: 'মালবাহী 9022 ক্ষমতার কাছে',
            conflictCountdown: '⚠ অমীমাংসিত সংঘর্ষ — স্বয়ংক্রিয় এস্কেলেশন:',
        },
        mr: {
            appName: 'इंटेलिजंट ट्रॅफिक ऑप्टिमा', ministry: 'रेल्वे मंत्रालय',
            navDashboard: 'डॅशबोर्ड', navSchedules: 'वेळापत्रक', navNetworkMap: 'नेटवर्क नकाशा', navReports: 'अहवाल',
            whatIfTitle: 'What-If सिम्युलेशन', scenarioLabel: 'परिस्थिती', scenarioFreight: 'मालवाहतूक विलंब (नोड A)',
            scenarioMaintenance: 'ट्रॅक देखभाल', scenarioWeather: 'हवामान इशारा',
            autoResolveLabel: 'स्वयं-विवाद निराकरण', runSimulation: 'सिम्युलेशन चालवा',
            dominoAlerts: 'डोमिनो विलंब इशारे', systemStatus: 'प्रणाली स्थिती', statusOptimal: 'उत्तम', statusConflict: 'विवाद',
            activeTrains: 'सक्रिय गाड्या', networkLoad: 'नेटवर्क भार', networkDelay: 'अंदाजित विलंब',
            stringChartTitle: 'वेळ-अंतर स्ट्रिंग चार्ट', btnExport: 'निर्यात', btnFilter: 'फिल्टर', btnAddRoute: '+ मार्ग जोडा',
            runningSimulation: 'सिम्युलेशन चालू आहे...', timetableTitle: 'गाडी वेळापत्रक आणि लाइव्ह स्थिती',
            thTrainNumber: 'गाडी क्रमांक', thTrainName: 'गाडीचे नाव', thType: 'प्रकार', thCurrentStation: 'सध्याचे स्टेशन',
            thScheduledArr: 'नियोजित आगमन', thExpectedArr: 'अपेक्षित आगमन', thStatus: 'स्थिती', statusOnTime: 'वेळेवर',
            trackSchematicTitle: 'ट्रॅक योजना आकृती', btnZoomIn: 'मोठे करा', btnZoomOut: 'लहान करा',
            reportsTitle: 'अडथळा आणि विवाद विश्लेषण', btnDownloadPDF: 'PDF डाउनलोड', btnShare: 'शेअर करा',
            impactTitle: 'राष्ट्रीय नेटवर्क प्रभाव मेट्रिक्स', impactSubtitle: 'सर्व झोनवरील ऑप्टिमायझेशन कामगिरीचा सारांश',
            fuelSaved: 'एकूण इंधन बचत', delaysPrevented: 'रोखलेले विलंब', networkThroughput: 'नेटवर्क थ्रूपुट',
            badgeCritical: 'गंभीर', badgeWarning: 'इशारा', badgeOptimized: 'ऑप्टिमाइझ्ड',
            card1Title: 'स्टेशन C सेक्टर 4 अडथळा', card2Title: 'स्टेशन B लूप लाइन क्षमता', card3Title: 'सेक्टर 1 वेळापत्रक ऑप्टिमायझेशन',
            card1Desc: 'एक्सप्रेस 20643 सिग्नल विवाद स्वयंचलित साइडिंगने सोडवला.', card2Desc: 'स्टेशन B वरील मालवाहतूक लूप लाइन क्षमतेजवळ.',
            card3Desc: 'कर्नाटक एक्सप्रेस स्वयंचलितपणे पुनर्निर्धारित.',
            cascadingDelayLabel: 'कॅस्केडिंग विलंब प्रक्षेपण', resolutionProgress: 'निराकरण प्रगती',
            loopOccupancyLabel: 'लूप लाइन अधिभोग', occupancyLevel: 'अधिभोग पातळी',
            throughputLabel: 'थ्रूपुट कार्यक्षमता (%)', efficiencyIndex: 'कार्यक्षमता निर्देशांक',
            metricDelaysPrevented: 'रोखलेले विलंब', metricEstSavings: 'अंदाजित बचत',
            metricWaitTime: 'प्रतीक्षा वेळ', metricRiskIndex: 'जोखीम निर्देशांक', elevated: 'वाढलेला',
            metricFuelSaved: 'इंधन बचत', metricThroughputGain: 'थ्रूपुट लाभ',
            auditTrailTitle: '🔒 एस्केलेशन ऑडिट ट्रेल', auditTime: 'वेळ', auditEvent: 'घटना',
            auditAction: 'केलेली कारवाई', auditOperator: 'ऑपरेटर', auditResult: 'निकाल',
            filterModalTitle: 'गाड्या फिल्टर करा', filterByType: 'प्रकारानुसार', filterByStatus: 'स्थितीनुसार',
            btnReset: 'रीसेट', btnApply: 'फिल्टर लागू करा', addRouteTitle: 'नवा मार्ग जोडा',
            newTrainNumber: 'गाडी क्रमांक', newTrainName: 'गाडीचे नाव', newTrainType: 'प्रकार',
            newOrigin: 'मूळ स्टेशन', newDestination: 'गंतव्य स्टेशन', btnCancel: 'रद्द करा',
            escalationTitle: '⚠️ स्टेशन C (ब्लॉक सेक्टर 4) वर अनुपस्थित गाडी विवाद',
            escalationSubtitle: '15 सेकंद अनिराकरण विवाद. स्वायत्त सुरक्षा प्रोटोकॉल सुरू.',
            broadcastLabel: '📡 आपत्कालीन प्रसारण:', broadcastMsg: 'स्टेशन मास्टर आणि S&T डिस्पॅचरला अलर्ट पाठवला.',
            escalationTimerLabel: 'स्वायत्त ओव्हरराइड उपलब्ध. कारवाई निवडा:',
            alert1: 'एक्सप्रेस 12041 विलंब सेक्टर 4 पर्यंत', alert2: 'मालवाहतूक 9022 क्षमतेजवळ',
            conflictCountdown: '⚠ अनुपस्थित विवाद — स्वयं-वृद्धी:',
        },
        kn: {
            appName: 'ಇಂಟೆಲಿಜೆಂಟ್ ಟ್ರಾಫಿಕ್ ಆಪ್ಟಿಮಾ', ministry: 'ರೈಲ್ವೇ ಸಚಿವಾಲಯ',
            navDashboard: 'ಡ್ಯಾಶ್‌ಬೋರ್ಡ್', navSchedules: 'ವೇಳಾಪಟ್ಟಿಗಳು', navNetworkMap: 'ನೆಟ್‌ವರ್ಕ್ ನಕ್ಷೆ', navReports: 'ವರದಿಗಳು',
            whatIfTitle: 'What-If ಸಿಮ್ಯುಲೇಶನ್', scenarioLabel: 'ಸನ್ನಿವೇಶ', scenarioFreight: 'ಸರಕು ವಿಳಂಬ (ನೋಡ್ A)',
            scenarioMaintenance: 'ಟ್ರ್ಯಾಕ್ ನಿರ್ವಹಣೆ', scenarioWeather: 'ಹವಾಮಾನ ಎಚ್ಚರಿಕೆ',
            autoResolveLabel: 'ಸ್ವಯಂ-ಸಂಘರ್ಷ ಪರಿಹಾರ', runSimulation: 'ಸಿಮ್ಯುಲೇಶನ್ ನಡೆಸಿ',
            dominoAlerts: 'ಡೊಮಿನೊ ವಿಳಂಬ ಎಚ್ಚರಿಕೆಗಳು', systemStatus: 'ಸಿಸ್ಟಮ್ ಸ್ಥಿತಿ', statusOptimal: 'ಅತ್ಯುತ್ತಮ', statusConflict: 'ಸಂಘರ್ಷ',
            activeTrains: 'ಸಕ್ರಿಯ ರೈಲುಗಳು', networkLoad: 'ನೆಟ್‌ವರ್ಕ್ ಲೋಡ್', networkDelay: 'ಅಂದಾಜು ವಿಳಂಬ',
            stringChartTitle: 'ಸಮಯ-ದೂರ ಸ್ಟ್ರಿಂಗ್ ಚಾರ್ಟ್', btnExport: 'ರಫ್ತು', btnFilter: 'ಫಿಲ್ಟರ್', btnAddRoute: '+ ಮಾರ್ಗ ಸೇರಿಸಿ',
            runningSimulation: 'ಸಿಮ್ಯುಲೇಶನ್ ನಡೆಯುತ್ತಿದೆ...', timetableTitle: 'ರೈಲು ವೇಳಾಪಟ್ಟಿ ಮತ್ತು ಲೈವ್',
            thTrainNumber: 'ರೈಲು ಸಂಖ್ಯೆ', thTrainName: 'ರೈಲು ಹೆಸರು', thType: 'ವಿಧ', thCurrentStation: 'ಪ್ರಸ್ತುತ ನಿಲ್ದಾಣ',
            thScheduledArr: 'ನಿಗದಿತ ಆಗಮನ', thExpectedArr: 'ನಿರೀಕ್ಷಿತ ಆಗಮನ', thStatus: 'ಸ್ಥಿತಿ', statusOnTime: 'ಸಮಯಕ್ಕೆ',
            trackSchematicTitle: 'ಟ್ರ್ಯಾಕ್ ಸ್ಕೀಮ್ಯಾಟಿಕ್', btnZoomIn: 'ಜೂಮ್ ಇನ್', btnZoomOut: 'ಜೂಮ್ ಔಟ್',
            reportsTitle: 'ಅಡಚಣೆ ಮತ್ತು ಸಂಘರ್ಷ ವಿಶ್ಲೇಷಣೆ', btnDownloadPDF: 'PDF ಡೌನ್‌ಲೋಡ್', btnShare: 'ಹಂಚಿ',
            impactTitle: 'ರಾಷ್ಟ್ರೀಯ ನೆಟ್‌ವರ್ಕ್ ಪ್ರಭಾವ', impactSubtitle: 'ಎಲ್ಲಾ ವಲಯಗಳ ಅತ್ಯುತ್ತಮಗೊಳಿಸುವಿಕೆ ಕಾರ್ಯಕ್ಷಮತೆ',
            fuelSaved: 'ಒಟ್ಟು ಇಂಧನ ಉಳಿತಾಯ', delaysPrevented: 'ತಡೆಯಲಾದ ವಿಳಂಬಗಳು', networkThroughput: 'ನೆಟ್‌ವರ್ಕ್ ಥ್ರೂಪುಟ್',
            badgeCritical: 'ನಿರ್ಣಾಯಕ', badgeWarning: 'ಎಚ್ಚರಿಕೆ', badgeOptimized: 'ಅತ್ಯುತ್ತಮ',
            card1Title: 'ನಿಲ್ದಾಣ C ಸೆಕ್ಟರ್ 4 ಅಡಚಣೆ', card2Title: 'ನಿಲ್ದಾಣ B ಲೂಪ್ ಲೈನ್ ಸಾಮರ್ಥ್ಯ', card3Title: 'ಸೆಕ್ಟರ್ 1 ವೇಳಾಪಟ್ಟಿ ಅತ್ಯುತ್ತಮಗೊಳಿಸುವಿಕೆ',
            card1Desc: 'ಎಕ್ಸ್‌ಪ್ರೆಸ್ 20643 ಸಿಗ್ನಲ್ ಸಂಘರ್ಷ ಸ್ವಯಂಚಾಲಿತ ಸೈಡಿಂಗ್ ಮೂಲಕ ಪರಿಹರಿಸಲಾಯಿತು.',
            card2Desc: 'ನಿಲ್ದಾಣ B ನಲ್ಲಿ ಸರಕು ಲೂಪ್ ಲೈನ್ ಸಾಮರ್ಥ್ಯ ತಲುಪುತ್ತಿದೆ.', card3Desc: 'ಕರ್ನಾಟಕ ಎಕ್ಸ್‌ಪ್ರೆಸ್ ಸ್ವಯಂಚಾಲಿತವಾಗಿ ಮರುನಿಗದಿಪಡಿಸಲಾಯಿತು.',
            cascadingDelayLabel: 'ಕ್ಯಾಸ್ಕೇಡಿಂಗ್ ವಿಳಂಬ ಪ್ರಕ್ಷೇಪಣ', resolutionProgress: 'ಪರಿಹಾರ ಪ್ರಗತಿ',
            loopOccupancyLabel: 'ಲೂಪ್ ಲೈನ್ ಆಕ್ಯುಪೆನ್ಸಿ', occupancyLevel: 'ಆಕ್ಯುಪೆನ್ಸಿ ಮಟ್ಟ',
            throughputLabel: 'ಥ್ರೂಪುಟ್ ದಕ್ಷತೆ (%)', efficiencyIndex: 'ದಕ್ಷತೆ ಸೂಚ್ಯಂಕ',
            metricDelaysPrevented: 'ತಡೆಯಲಾದ ವಿಳಂಬ', metricEstSavings: 'ಅಂದಾಜು ಉಳಿತಾಯ',
            metricWaitTime: 'ಕಾಯುವ ಸಮಯ', metricRiskIndex: 'ಅಪಾಯ ಸೂಚ್ಯಂಕ', elevated: 'ಏರಿಕೆ',
            metricFuelSaved: 'ಇಂಧನ ಉಳಿತಾಯ', metricThroughputGain: 'ಥ್ರೂಪುಟ್ ಲಾಭ',
            auditTrailTitle: '🔒 ಎಸ್ಕಲೇಶನ್ ಆಡಿಟ್ ಟ್ರೇಲ್', auditTime: 'ಸಮಯ', auditEvent: 'ಘಟನೆ',
            auditAction: 'ತೆಗೆದುಕೊಂಡ ಕ್ರಮ', auditOperator: 'ಆಪರೇಟರ್', auditResult: 'ಫಲಿತಾಂಶ',
            filterModalTitle: 'ರೈಲುಗಳನ್ನು ಫಿಲ್ಟರ್ ಮಾಡಿ', filterByType: 'ವಿಧದ ಪ್ರಕಾರ', filterByStatus: 'ಸ್ಥಿತಿ ಪ್ರಕಾರ',
            btnReset: 'ಮರುಹೊಂದಿಸಿ', btnApply: 'ಫಿಲ್ಟರ್ ಅನ್ವಯಿಸಿ', addRouteTitle: 'ಹೊಸ ಮಾರ್ಗ ಸೇರಿಸಿ',
            newTrainNumber: 'ರೈಲು ಸಂಖ್ಯೆ', newTrainName: 'ರೈಲು ಹೆಸರು', newTrainType: 'ವಿಧ',
            newOrigin: 'ಮೂಲ ನಿಲ್ದಾಣ', newDestination: 'ಗಮ್ಯ ನಿಲ್ದಾಣ', btnCancel: 'ರದ್ದುಮಾಡಿ',
            escalationTitle: '⚠️ ನಿಲ್ದಾಣ C (ಬ್ಲಾಕ್ ಸೆಕ್ಟರ್ 4) ನಲ್ಲಿ ಗಮನಿಸದ ರೈಲು ಸಂಘರ್ಷ',
            escalationSubtitle: '15 ಸೆಕೆಂಡು ಪರಿಹರಿಸದ ಸಂಘರ್ಷ. ಸ್ವಾಯತ್ತ ಸುರಕ್ಷತಾ ಪ್ರೋಟೋಕಾಲ್ ಪ್ರಾರಂಭ.',
            broadcastLabel: '📡 ತುರ್ತು ಪ್ರಸಾರ:', broadcastMsg: 'ನಿಲ್ದಾಣ ಮಾಸ್ಟರ್ ಮತ್ತು S&T ಡಿಸ್ಪ್ಯಾಚರ್‌ಗೆ ಎಚ್ಚರಿಕೆ ಕಳುಹಿಸಲಾಗಿದೆ.',
            escalationTimerLabel: 'ಸ್ವಾಯತ್ತ ಓವರ್‌ರೈಡ್ ಲಭ್ಯ. ಕ್ರಮ ಆಯ್ಕೆಮಾಡಿ:',
            alert1: 'ಎಕ್ಸ್‌ಪ್ರೆಸ್ 12041 ವಿಳಂಬ ಸೆಕ್ಟರ್ 4 ವರೆಗೆ', alert2: 'ಸರಕು 9022 ಸಾಮರ್ಥ್ಯ ಬಳಿ',
            conflictCountdown: '⚠ ಗಮನಿಸದ ಸಂಘರ್ಷ — ಸ್ವಯಂ-ಏರಿಕೆ:',
        },
        ml: {
            appName: 'ഇന്റലിജന്റ് ട്രാഫിക് ഒപ്റ്റിമ', ministry: 'റെയിൽവേ മന്ത്രാലയം',
            navDashboard: 'ഡാഷ്ബോർഡ്', navSchedules: 'ഷെഡ്യൂളുകൾ', navNetworkMap: 'നെറ്റ്‌വർക്ക് മാപ്പ്', navReports: 'റിപ്പോർട്ടുകൾ',
            whatIfTitle: 'What-If സിമുലേഷൻ', scenarioLabel: 'സാഹചര്യം', scenarioFreight: 'ചരക്ക് കാലതാമസം (നോഡ് A)',
            scenarioMaintenance: 'ട്രാക്ക് പരിപാലനം', scenarioWeather: 'കാലാവസ്ഥ മുന്നറിയിപ്പ്',
            autoResolveLabel: 'ഓട്ടോ-പരിഹാരം', runSimulation: 'സിമുലേഷൻ നടത്തുക',
            dominoAlerts: 'ഡൊമിനോ കാലതാമസ അലേർട്ടുകൾ', systemStatus: 'സിസ്റ്റം നില', statusOptimal: 'ഒപ്റ്റിമൽ', statusConflict: 'സംഘർഷം',
            activeTrains: 'സജീവ ട്രെയിനുകൾ', networkLoad: 'നെറ്റ്‌വർക്ക് ലോഡ്', networkDelay: 'കണക്കാക്കിയ കാലതാമസം',
            stringChartTitle: 'സമയ-ദൂര സ്ട്രിങ് ചാർട്ട്', btnExport: 'കയറ്റുമതി', btnFilter: 'ഫിൽട്ടർ', btnAddRoute: '+ റൂട്ട് ചേർക്കുക',
            runningSimulation: 'സിമുലേഷൻ നടക്കുന്നു...', timetableTitle: 'ട്രെയിൻ ടൈംടേബിൾ & ലൈവ്',
            thTrainNumber: 'ട്രെയിൻ നമ്പർ', thTrainName: 'ട്രെയിൻ പേര്', thType: 'തരം', thCurrentStation: 'നിലവിലെ സ്റ്റേഷൻ',
            thScheduledArr: 'ഷെഡ്യൂൾ ചെയ്ത വരവ്', thExpectedArr: 'പ്രതീക്ഷിക്കുന്ന വരവ്', thStatus: 'നില', statusOnTime: 'സമയത്തിന്',
            trackSchematicTitle: 'ട്രാക്ക് സ്കീമാറ്റിക്', btnZoomIn: 'സൂം ഇൻ', btnZoomOut: 'സൂം ഔട്ട്',
            reportsTitle: 'തടസ്സ & സംഘർഷ വിശകലനം', btnDownloadPDF: 'PDF ഡൗൺലോഡ്', btnShare: 'ഷെയർ',
            impactTitle: 'ദേശീയ നെറ്റ്‌വർക്ക് ആഘാതം', impactSubtitle: 'എല്ലാ സോണുകളിലെ ഒപ്റ്റിമൈസേഷൻ പ്രകടനം',
            fuelSaved: 'ആകെ ഇന്ധന ലാഭം', delaysPrevented: 'തടഞ്ഞ കാലതാമസങ്ങൾ', networkThroughput: 'നെറ്റ്‌വർക്ക് ത്രൂപുട്ട്',
            badgeCritical: 'ഗുരുതരം', badgeWarning: 'മുന്നറിയിപ്പ്', badgeOptimized: 'ഒപ്റ്റിമൈസ്ഡ്',
            card1Title: 'സ്റ്റേഷൻ C സെക്ടർ 4 തടസ്സം', card2Title: 'സ്റ്റേഷൻ B ലൂപ്പ് ലൈൻ ശേഷി', card3Title: 'സെക്ടർ 1 ഷെഡ്യൂൾ ഒപ്റ്റിമൈസേഷൻ',
            card1Desc: 'എക്സ്‌പ്രസ് 20643 സിഗ്നൽ സംഘർഷം ഓട്ടോ സൈഡിങ് വഴി പരിഹരിച്ചു.',
            card2Desc: 'സ്റ്റേഷൻ B യിലെ ചരക്ക് ലൂപ്പ് ലൈൻ ശേഷിയോട് അടുക്കുന്നു.', card3Desc: 'കർണ്ണാടക എക്സ്‌പ്രസ് സ്വയം പുനഃക്രമീകരിച്ചു.',
            cascadingDelayLabel: 'കാലതാമസ പ്രൊജക്ഷൻ', resolutionProgress: 'പരിഹാര പുരോഗതി',
            loopOccupancyLabel: 'ലൂപ്പ് ലൈൻ ഓക്യുപൻസി', occupancyLevel: 'ഓക്യുപൻസി ലെവൽ',
            throughputLabel: 'ത്രൂപുട്ട് (%)', efficiencyIndex: 'ദക്ഷത സൂചിക',
            metricDelaysPrevented: 'തടഞ്ഞ കാലതാമസങ്ങൾ', metricEstSavings: 'കണക്കാക്കിയ ലാഭം',
            metricWaitTime: 'കാത്തിരിപ്പ് സമയം', metricRiskIndex: 'അപകട സൂചിക', elevated: 'ഉയർന്നത്',
            metricFuelSaved: 'ഇന്ധന ലാഭം', metricThroughputGain: 'ത്രൂപുട്ട് നേട്ടം',
            auditTrailTitle: '🔒 എസ്കലേഷൻ ഓഡിറ്റ് ട്രെയിൽ', auditTime: 'സമയം', auditEvent: 'സംഭവം',
            auditAction: 'എടുത്ത നടപടി', auditOperator: 'ഓപ്പറേറ്റർ', auditResult: 'ഫലം',
            filterModalTitle: 'ട്രെയിനുകൾ ഫിൽട്ടർ', filterByType: 'തരം അനുസരിച്ച്', filterByStatus: 'നില അനുസരിച്ച്',
            btnReset: 'റീസെറ്റ്', btnApply: 'ഫിൽട്ടറുകൾ ബാധകമാക്കുക', addRouteTitle: 'പുതിയ റൂട്ട് ചേർക്കുക',
            newTrainNumber: 'ട്രെയിൻ നമ്പർ', newTrainName: 'ട്രെയിൻ പേര്', newTrainType: 'തരം',
            newOrigin: 'ഉത്ഭവ സ്റ്റേഷൻ', newDestination: 'ലക്ഷ്യ സ്റ്റേഷൻ', btnCancel: 'റദ്ദാക്കുക',
            escalationTitle: '⚠️ സ്റ്റേഷൻ C (ബ്ലോക്ക് സെക്ടർ 4) ൽ ശ്രദ്ധിക്കാത്ത ട്രെയിൻ സംഘർഷം',
            escalationSubtitle: '15 സെക്കൻഡ് പരിഹരിക്കാത്ത സംഘർഷം. ഓട്ടോണോമസ് സേഫ്റ്റി പ്രോട്ടോക്കോൾ ആരംഭിച്ചു.',
            broadcastLabel: '📡 അടിയന്തര പ്രക്ഷേപണം:', broadcastMsg: 'സ്റ്റേഷൻ മാസ്റ്ററിനും S&T ഡിസ്‌പാച്ചർക്കും അലേർട്ട് അയച്ചു.',
            escalationTimerLabel: 'ഓട്ടോണോമസ് ഓവർറൈഡ് ലഭ്യം. നടപടി തിരഞ്ഞെടുക്കുക:',
            alert1: 'എക്സ്‌പ്രസ് 12041 കാലതാമസം സെക്ടർ 4 വരെ', alert2: 'ചരക്ക് 9022 ശേഷിയോട് അടുക്കുന്നു',
            conflictCountdown: '⚠ ശ്രദ്ധിക്കാത്ത സംഘർഷം — ഓട്ടോ-എസ്കലേഷൻ:',
        },
        gu: {
            appName: 'ઇન્ટેલિજન્ટ ટ્રાફિક ઓપ્ટિમા', ministry: 'રેલ્વે મંત્રાલય',
            navDashboard: 'ડેશબોર્ડ', navSchedules: 'સમયપત્રક', navNetworkMap: 'નેટવર્ક નકશો', navReports: 'અહેવાલો',
            whatIfTitle: 'What-If સિમ્યુલેશન', scenarioLabel: 'દૃશ્ય', scenarioFreight: 'માલવાહક વિલંબ (નોડ A)',
            scenarioMaintenance: 'ટ્રેક જાળવણી', scenarioWeather: 'હવામાન ચેતવણી',
            autoResolveLabel: 'સ્વયં-સંઘર્ષ નિરાકરણ', runSimulation: 'સિમ્યુલેશન ચલાવો',
            dominoAlerts: 'ડોમિનો વિલંબ ચેતવણીઓ', systemStatus: 'સિસ્ટમ સ્થિતિ', statusOptimal: 'શ્રેષ્ઠ', statusConflict: 'સંઘર્ષ',
            activeTrains: 'સક્રિય ટ્રેનો', networkLoad: 'નેટવર્ક લોડ', networkDelay: 'અંદાજિત વિલંબ',
            stringChartTitle: 'સમય-અંતર સ્ટ્રિંગ ચાર્ટ', btnExport: 'નિકાસ', btnFilter: 'ફિલ્ટર', btnAddRoute: '+ માર્ગ ઉમેરો',
            runningSimulation: 'સિમ્યુલેશન ચાલુ છે...', timetableTitle: 'ટ્રેન સમયપત્રક અને લાઈવ',
            thTrainNumber: 'ટ્રેન નંબર', thTrainName: 'ટ્રેન નામ', thType: 'પ્રકાર', thCurrentStation: 'વર્તમાન સ્ટેશન',
            thScheduledArr: 'નિર્ધારિત આગમન', thExpectedArr: 'અપેક્ષિત આગમન', thStatus: 'સ્થિતિ', statusOnTime: 'સમયસર',
            trackSchematicTitle: 'ટ્રેક સ્કીમેટિક', btnZoomIn: 'ઝૂમ ઇન', btnZoomOut: 'ઝૂમ આઉટ',
            reportsTitle: 'અવરોધ અને સંઘર્ષ વિશ્લેષણ', btnDownloadPDF: 'PDF ડાઉનલોડ', btnShare: 'શેર કરો',
            impactTitle: 'રાષ્ટ્રીય નેટવર્ક પ્રભાવ', impactSubtitle: 'બધા ઝોનના ઓપ્ટિમાઇઝેશન પ્રદર્શનનો સારાંશ',
            fuelSaved: 'કુલ ઈંધણ બચત', delaysPrevented: 'અટકાવેલ વિલંબ', networkThroughput: 'નેટવર્ક થ્રૂપુટ',
            badgeCritical: 'ગંભીર', badgeWarning: 'ચેતવણી', badgeOptimized: 'ઓપ્ટિમાઇઝ્ડ',
            card1Title: 'સ્ટેશન C સેક્ટર 4 અવરોધ', card2Title: 'સ્ટેશન B લૂપ લાઇન ક્ષમતા', card3Title: 'સેક્ટર 1 સમયપત્રક ઓપ્ટિમાઇઝેશન',
            card1Desc: 'એક્સપ્રેસ 20643 સિગ્નલ સંઘર્ષ ઓટો સાઇડિંગ દ્વારા ઉકેલાયો.',
            card2Desc: 'સ્ટેશન B પર માલવાહક લૂપ લાઇન ક્ષમતા નજીક.', card3Desc: 'કર્ણાટક એક્સપ્રેસ સ્વયં પુનઃનિર્ધારિત.',
            cascadingDelayLabel: 'કેસ્કેડિંગ વિલંબ પ્રક્ષેપણ', resolutionProgress: 'નિરાકરણ પ્રગતિ',
            loopOccupancyLabel: 'લૂપ લાઇન ઓક્યુપન્સી', occupancyLevel: 'ઓક્યુપન્સી સ્તર',
            throughputLabel: 'થ્રૂપુટ (%)', efficiencyIndex: 'કાર્યક્ષમતા સૂચકાંક',
            metricDelaysPrevented: 'અટકાવેલ વિલંબ', metricEstSavings: 'અંદાજિત બચત',
            metricWaitTime: 'પ્રતીક્ષા સમય', metricRiskIndex: 'જોખમ સૂચકાંક', elevated: 'ઉન્નત',
            metricFuelSaved: 'ઈંધણ બચત', metricThroughputGain: 'થ્રૂપુટ લાભ',
            auditTrailTitle: '🔒 એસ્કેલેશન ઓડિટ ટ્રેઇલ', auditTime: 'સમય', auditEvent: 'ઘટના',
            auditAction: 'લીધેલ પગલું', auditOperator: 'ઓપરેટર', auditResult: 'પરિણામ',
            filterModalTitle: 'ટ્રેનો ફિલ્ટર કરો', filterByType: 'પ્રકાર દ્વારા', filterByStatus: 'સ્થિતિ દ્વારા',
            btnReset: 'રીસેટ', btnApply: 'ફિલ્ટર લાગુ કરો', addRouteTitle: 'નવો માર્ગ ઉમેરો',
            newTrainNumber: 'ટ્રેન નંબર', newTrainName: 'ટ્રેન નામ', newTrainType: 'પ્રકાર',
            newOrigin: 'ઉદ્ગમ સ્ટેશન', newDestination: 'ગંતવ્ય સ્ટેશન', btnCancel: 'રદ કરો',
            escalationTitle: '⚠️ સ્ટેશન C (બ્લોક સેક્ટર 4) પર અનુપસ્થિત ટ્રેન સંઘર્ષ',
            escalationSubtitle: '15 સેકન્ડથી અનિરાકૃત સંઘર્ષ. સ્વાયત્ત સુરક્ષા પ્રોટોકોલ શરૂ.',
            broadcastLabel: '📡 કટોકટી પ્રસારણ:', broadcastMsg: 'સ્ટેશન માસ્ટર અને S&T ડિસ્પેચરને ચેતવણી મોકલી.',
            escalationTimerLabel: 'સ્વાયત્ત ઓવરરાઇડ ઉપલબ્ધ. કાર્યવાહી પસંદ કરો:',
            alert1: 'એક્સપ્રેસ 12041 વિલંબ સેક્ટર 4 સુધી', alert2: 'માલવાહક 9022 ક્ષમતા નજીક',
            conflictCountdown: '⚠ અનુપસ્થિત સંઘર્ષ — સ્વયં-વૃદ્ધિ:',
        },
        pa: {
            appName: 'ਇੰਟੈਲੀਜੈਂਟ ਟ੍ਰੈਫਿਕ ਔਪਟੀਮਾ', ministry: 'ਰੇਲਵੇ ਮੰਤਰਾਲਾ',
            navDashboard: 'ਡੈਸ਼ਬੋਰਡ', navSchedules: 'ਸਮਾਂ-ਸੂਚੀ', navNetworkMap: 'ਨੈੱਟਵਰਕ ਨਕਸ਼ਾ', navReports: 'ਰਿਪੋਰਟਾਂ',
            whatIfTitle: 'What-If ਸਿਮੂਲੇਸ਼ਨ', scenarioLabel: 'ਦ੍ਰਿਸ਼', scenarioFreight: 'ਮਾਲ ਗੱਡੀ ਦੇਰੀ (ਨੋਡ A)',
            scenarioMaintenance: 'ਟ੍ਰੈਕ ਦੇਖ-ਭਾਲ', scenarioWeather: 'ਮੌਸਮ ਚੇਤਾਵਨੀ',
            autoResolveLabel: 'ਆਟੋ-ਹੱਲ ਝਗੜੇ', runSimulation: 'ਸਿਮੂਲੇਸ਼ਨ ਚਲਾਓ',
            dominoAlerts: 'ਡੋਮਿਨੋ ਦੇਰੀ ਚੇਤਾਵਨੀਆਂ', systemStatus: 'ਸਿਸਟਮ ਸਥਿਤੀ', statusOptimal: 'ਵਧੀਆ', statusConflict: 'ਟਕਰਾਅ',
            activeTrains: 'ਸਰਗਰਮ ਰੇਲ ਗੱਡੀਆਂ', networkLoad: 'ਨੈੱਟਵਰਕ ਲੋਡ', networkDelay: 'ਅੰਦਾਜ਼ਨ ਦੇਰੀ',
            stringChartTitle: 'ਸਮਾਂ-ਦੂਰੀ ਸਤਰ ਚਾਰਟ', btnExport: 'ਨਿਰਯਾਤ', btnFilter: 'ਫਿਲਟਰ', btnAddRoute: '+ ਰਸਤਾ ਜੋੜੋ',
            runningSimulation: 'ਸਿਮੂਲੇਸ਼ਨ ਚੱਲ ਰਹੀ ਹੈ...', timetableTitle: 'ਰੇਲ ਸਮਾਂ-ਸੂਚੀ ਅਤੇ ਲਾਈਵ',
            thTrainNumber: 'ਰੇਲ ਨੰਬਰ', thTrainName: 'ਰੇਲ ਨਾਮ', thType: 'ਕਿਸਮ', thCurrentStation: 'ਮੌਜੂਦਾ ਸਟੇਸ਼ਨ',
            thScheduledArr: 'ਨਿਰਧਾਰਤ ਆਗਮਨ', thExpectedArr: 'ਉਮੀਦ ਆਗਮਨ', thStatus: 'ਸਥਿਤੀ', statusOnTime: 'ਸਮੇਂ \'ਤੇ',
            trackSchematicTitle: 'ਟ੍ਰੈਕ ਸਕੀਮੈਟਿਕ', btnZoomIn: 'ਜ਼ੂਮ ਇਨ', btnZoomOut: 'ਜ਼ੂਮ ਆਊਟ',
            reportsTitle: 'ਰੁਕਾਵਟ ਅਤੇ ਟਕਰਾਅ ਵਿਸ਼ਲੇਸ਼ਣ', btnDownloadPDF: 'PDF ਡਾਊਨਲੋਡ', btnShare: 'ਸਾਂਝਾ ਕਰੋ',
            impactTitle: 'ਰਾਸ਼ਟਰੀ ਨੈੱਟਵਰਕ ਪ੍ਰਭਾਵ', impactSubtitle: 'ਸਾਰੇ ਜ਼ੋਨਾਂ ਦੀ ਕਾਰਗੁਜ਼ਾਰੀ ਸੰਖੇਪ',
            fuelSaved: 'ਕੁੱਲ ਈਂਧਨ ਬੱਚਤ', delaysPrevented: 'ਰੋਕੀਆਂ ਦੇਰੀਆਂ', networkThroughput: 'ਨੈੱਟਵਰਕ ਥ੍ਰੂਪੁੱਟ',
            badgeCritical: 'ਗੰਭੀਰ', badgeWarning: 'ਚੇਤਾਵਨੀ', badgeOptimized: 'ਔਪਟੀਮਾਈਜ਼ਡ',
            card1Title: 'ਸਟੇਸ਼ਨ C ਸੈਕਟਰ 4 ਰੁਕਾਵਟ', card2Title: 'ਸਟੇਸ਼ਨ B ਲੂਪ ਲਾਈਨ ਸਮਰੱਥਾ', card3Title: 'ਸੈਕਟਰ 1 ਸਮਾਂ-ਸੂਚੀ ਔਪਟੀਮਾਈਜ਼ੇਸ਼ਨ',
            card1Desc: 'ਐਕਸਪ੍ਰੈੱਸ 20643 ਸਿਗਨਲ ਟਕਰਾਅ ਆਟੋ ਸਾਈਡਿੰਗ ਨਾਲ ਹੱਲ ਹੋਇਆ.',
            card2Desc: 'ਸਟੇਸ਼ਨ B \'ਤੇ ਮਾਲ ਲੂਪ ਲਾਈਨ ਸਮਰੱਥਾ ਨੇੜੇ.', card3Desc: 'ਕਰਨਾਟਕ ਐਕਸਪ੍ਰੈੱਸ ਆਪਣੇ ਆਪ ਮੁੜ ਨਿਰਧਾਰਤ.',
            cascadingDelayLabel: 'ਕੈਸਕੇਡਿੰਗ ਦੇਰੀ ਅਨੁਮਾਨ', resolutionProgress: 'ਹੱਲ ਪ੍ਰਗਤੀ',
            loopOccupancyLabel: 'ਲੂਪ ਲਾਈਨ ਕਿੱਤਾ', occupancyLevel: 'ਕਿੱਤਾ ਪੱਧਰ',
            throughputLabel: 'ਥ੍ਰੂਪੁੱਟ (%)', efficiencyIndex: 'ਕੁਸ਼ਲਤਾ ਸੂਚਕ',
            metricDelaysPrevented: 'ਰੋਕੀਆਂ ਦੇਰੀਆਂ', metricEstSavings: 'ਅੰਦਾਜ਼ਨ ਬੱਚਤ',
            metricWaitTime: 'ਉਡੀਕ ਸਮਾਂ', metricRiskIndex: 'ਖ਼ਤਰਾ ਸੂਚਕ', elevated: 'ਵਧਿਆ',
            metricFuelSaved: 'ਈਂਧਨ ਬੱਚਤ', metricThroughputGain: 'ਥ੍ਰੂਪੁੱਟ ਲਾਭ',
            auditTrailTitle: '🔒 ਐਸਕੇਲੇਸ਼ਨ ਆਡਿਟ ਟ੍ਰੇਲ', auditTime: 'ਸਮਾਂ', auditEvent: 'ਘਟਨਾ',
            auditAction: 'ਕੀਤੀ ਕਾਰਵਾਈ', auditOperator: 'ਆਪਰੇਟਰ', auditResult: 'ਨਤੀਜਾ',
            filterModalTitle: 'ਰੇਲ ਗੱਡੀਆਂ ਫਿਲਟਰ ਕਰੋ', filterByType: 'ਕਿਸਮ ਅਨੁਸਾਰ', filterByStatus: 'ਸਥਿਤੀ ਅਨੁਸਾਰ',
            btnReset: 'ਰੀਸੈੱਟ', btnApply: 'ਫਿਲਟਰ ਲਾਗੂ ਕਰੋ', addRouteTitle: 'ਨਵਾਂ ਰਸਤਾ ਜੋੜੋ',
            newTrainNumber: 'ਰੇਲ ਨੰਬਰ', newTrainName: 'ਰੇਲ ਨਾਮ', newTrainType: 'ਕਿਸਮ',
            newOrigin: 'ਸ਼ੁਰੂਆਤੀ ਸਟੇਸ਼ਨ', newDestination: 'ਮੰਜ਼ਿਲ ਸਟੇਸ਼ਨ', btnCancel: 'ਰੱਦ ਕਰੋ',
            escalationTitle: '⚠️ ਸਟੇਸ਼ਨ C (ਬਲਾਕ ਸੈਕਟਰ 4) \'ਤੇ ਅਣਦੇਖੀ ਰੇਲ ਟਕਰਾਅ',
            escalationSubtitle: '15 ਸਕਿੰਟ ਤੋਂ ਅਣਸੁਲਝਿਆ ਟਕਰਾਅ. ਸਵੈਚਾਲਤ ਸੁਰੱਖਿਆ ਪ੍ਰੋਟੋਕੋਲ ਸ਼ੁਰੂ.',
            broadcastLabel: '📡 ਐਮਰਜੈਂਸੀ ਪ੍ਰਸਾਰਣ:', broadcastMsg: 'ਸਟੇਸ਼ਨ ਮਾਸਟਰ ਅਤੇ S&T ਡਿਸਪੈਚਰ ਨੂੰ ਚੇਤਾਵਨੀ ਭੇਜੀ.',
            escalationTimerLabel: 'ਸਵੈਚਾਲਤ ਓਵਰਰਾਈਡ ਉਪਲਬਧ. ਕਾਰਵਾਈ ਚੁਣੋ:',
            alert1: 'ਐਕਸਪ੍ਰੈੱਸ 12041 ਦੇਰੀ ਸੈਕਟਰ 4 ਤੱਕ', alert2: 'ਮਾਲ ਗੱਡੀ 9022 ਸਮਰੱਥਾ ਨੇੜੇ',
            conflictCountdown: '⚠ ਅਣਦੇਖੀ ਟਕਰਾਅ — ਸਵੈ-ਵਧਾਈ:',
        }
    };

    let currentLang = 'en';

    /**
     * Applies localization: traverses all [data-i18n] elements and updates text.
     */
    function setLanguage(lang) {
        if (!i18n[lang]) return;
        currentLang = lang;
        const dict = i18n[lang];
        document.querySelectorAll('[data-i18n]').forEach(el => {
            const key = el.getAttribute('data-i18n');
            if (dict[key] !== undefined) {
                el.textContent = dict[key];
            }
        });
    }

    document.getElementById('lang-selector').addEventListener('change', (e) => {
        setLanguage(e.target.value);
    });


    /* ══════════════════════════════════════════════
       2. DOM References
       ══════════════════════════════════════════════ */

    const canvas = document.getElementById('string-chart');
    const ctx = canvas.getContext('2d');
    const runSimulationBtn = document.getElementById('run-simulation-btn');
    const loadingOverlay = document.getElementById('loading-overlay');
    const activeTimeEl = document.getElementById('active-time');
    const systemStatusEl = document.getElementById('system-status-value');
    const networkDelayEl = document.getElementById('network-delay-value');


    /* ══════════════════════════════════════════════
       3. Chart Configuration & State
       ══════════════════════════════════════════════ */

    const chartConfig = {
        padding: 50,
        gridColor: '#D3985833',
        textColor: '#6F5143',
        font: "12px -apple-system, BlinkMacSystemFont, \"SF Pro Display\", \"Inter\", sans-serif",
        lines: [
            { id: '20643', startStation: 0, endStation: 4, startTime: 0, endTime: 100, color: '#85431E', type: 'premium' },
            { id: '12637', startStation: 4, endStation: 0, startTime: 20, endTime: 120, color: '#34150F', type: 'passenger' },
            { id: 'BOXN-77', startStation: 4, endStation: 1, startTime: 40, endTime: 105, color: '#D39858', type: 'freight', dashed: true }
        ],
        stations: ['Erode Junction (ED)', 'Karur Junction (KRR)', 'Dindigul Junction (DG)', 'Madurai Junction (MDU)', 'Tirunelveli Junction (TEN)'],
        timeSpan: 180
    };

    // Mutable state
    let zoomLevel = 1.0;
    let activeFilters = { types: ['premium', 'passenger', 'freight'], statuses: ['ontime', 'delayed', 'rerouted'] };
    let conflictActive = true; // Start with a conflict active (20643 at Dindigul Junction)
    let conflictTimerId = null;
    let conflictCountdownSec = 15;
    let escalationTriggered = false;
    let isModalOpen = false;
    let escalationToast = null;
    const auditLog = [];


    /* ══════════════════════════════════════════════
       4. Real-Time Clock
       ══════════════════════════════════════════════ */

    function updateClock() {
        const now = new Date();
        activeTimeEl.textContent = now.toLocaleTimeString('en-US', {
            hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit'
        });
    }
    setInterval(updateClock, 1000);
    updateClock();


    /* ══════════════════════════════════════════════
       5. String Chart Drawing
       ══════════════════════════════════════════════ */

    function resizeCanvas() {
        const container = canvas.parentElement;
        const { width, height } = container.getBoundingClientRect();
        const devicePixelRatio = window.devicePixelRatio || 1;
        canvas.width = Math.round(width * devicePixelRatio);
        canvas.height = Math.round(height * devicePixelRatio);
        canvas.style.width = `${width}px`;
        canvas.style.height = `${height}px`;
        ctx.setTransform(devicePixelRatio, 0, 0, devicePixelRatio, 0, 0);
        drawChart();
    }

    function drawGrid() {
        const width = canvas.clientWidth;
        const height = canvas.clientHeight;
        const { padding, gridColor, textColor, font, stations, timeSpan } = chartConfig;

        ctx.clearRect(0, 0, width, height);
        ctx.strokeStyle = gridColor;
        ctx.fillStyle = textColor;
        ctx.font = font;
        ctx.textAlign = 'right';
        ctx.textBaseline = 'middle';
        ctx.lineWidth = 1;

        const chartWidth = width - (padding * 2);
        const chartHeight = height - (padding * 2);

        // Horizontal lines (stations)
        const stationSpacing = chartHeight / (stations.length - 1);
        stations.forEach((station, index) => {
            const y = padding + (index * stationSpacing);
            ctx.beginPath();
            ctx.moveTo(padding, y);
            ctx.lineTo(width - padding, y);
            ctx.stroke();
            ctx.fillText(station, padding - 10, y);
        });

        // Vertical lines (time)
        ctx.textAlign = 'center';
        ctx.textBaseline = 'top';
        const timeIntervals = 6;
        const timeSpacing = chartWidth / timeIntervals;

        for (let i = 0; i <= timeIntervals; i++) {
            const x = padding + (i * timeSpacing);
            ctx.beginPath();
            ctx.moveTo(x, padding);
            ctx.lineTo(x, height - padding);
            ctx.stroke();
            ctx.fillText(`+${i * 30}m`, x, height - padding + 12);
        }
    }

    function drawTrainLines(linesData) {
        const width = canvas.clientWidth;
        const height = canvas.clientHeight;
        const { padding, stations, timeSpan } = chartConfig;

        const chartWidth = width - (padding * 2);
        const chartHeight = height - (padding * 2);
        const stationSpacing = chartHeight / (stations.length - 1);

        linesData.forEach(line => {
            const startX = padding + ((line.startTime / timeSpan) * chartWidth);
            const startY = padding + (line.startStation * stationSpacing);
            const endX = padding + ((line.endTime / timeSpan) * chartWidth);
            const endY = padding + (line.endStation * stationSpacing);

            ctx.strokeStyle = line.color;
            ctx.lineWidth = line.highlight ? 4 : 2.5;
            ctx.setLineDash(line.dashed ? [6, 4] : []);

            ctx.beginPath();
            ctx.moveTo(startX, startY);
            if (line.bend) {
                const controlX = (startX + endX) / 2;
                const controlY = (startY + endY) / 2 + (line.bendOffset || 0);
                ctx.quadraticCurveTo(controlX, controlY, endX, endY);
            } else {
                ctx.lineTo(endX, endY);
            }
            ctx.stroke();
            ctx.setLineDash([]);

            // Label at midpoint
            const midX = (startX + endX) / 2;
            const midY = (startY + endY) / 2;
            ctx.fillStyle = line.color;
            ctx.font = "bold 11px 'Inter', sans-serif";
            ctx.textAlign = 'center';
            ctx.textBaseline = 'bottom';

            ctx.save();
            ctx.translate(midX, midY - 6);
            const angle = Math.atan2(endY - startY, endX - startX);
            ctx.rotate(angle > Math.PI / 2 || angle < -Math.PI / 2 ? angle + Math.PI : angle);
            ctx.fillText(line.id, 0, 0);
            ctx.restore();
        });

        ctx.lineWidth = 1;
    }

    function drawChart(linesToDraw) {
        drawGrid();
        const lines = linesToDraw || getFilteredChartLines();
        drawTrainLines(lines);
    }

    function getFilteredChartLines() {
        return chartConfig.lines.filter(l => activeFilters.types.includes(l.type));
    }


    /* ══════════════════════════════════════════════
       6. Navigation — Tab Switching
       ══════════════════════════════════════════════ */

    const navItems = document.querySelectorAll('.sidebar-nav li');
    const stages = {
        'dashboard': document.getElementById('dashboard-stage'),
        'schedules': document.getElementById('schedules-stage'),
        'network-map': document.getElementById('network-map-stage'),
        'reports': document.getElementById('reports-stage')
    };

    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const targetTab = item.getAttribute('data-tab');
            if (!targetTab || !stages[targetTab]) return;

            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            Object.keys(stages).forEach(key => {
                stages[key].classList.toggle('hidden', key !== targetTab);
            });

            if (targetTab === 'dashboard') {
                setTimeout(resizeCanvas, 60);
            }
        });
    });


    /* ══════════════════════════════════════════════
       7. Toast Notification System
       ══════════════════════════════════════════════ */

    function showToast(message, type = 'success') {
        if (isModalOpen && type === 'critical') return null;
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        container.appendChild(toast);

        setTimeout(() => {
            toast.classList.add('toast-out');
            setTimeout(() => toast.remove(), 300);
        }, 3500);
        return toast;
    }

    function dismissEscalationToast() {
        if (!escalationToast) return;
        escalationToast.remove();
        escalationToast = null;
    }


    /* ══════════════════════════════════════════════
       8. Filter Modal
       ══════════════════════════════════════════════ */

    const filterModal = document.getElementById('filter-modal');
    const filterChips = document.querySelectorAll('.filter-chip');

    function openFilterModal() { filterModal.classList.add('active'); }
    function closeFilterModal() { filterModal.classList.remove('active'); }

    document.getElementById('filter-modal-close').addEventListener('click', closeFilterModal);
    document.getElementById('btn-filter-dashboard').addEventListener('click', openFilterModal);
    document.getElementById('btn-filter-schedules').addEventListener('click', openFilterModal);

    filterChips.forEach(chip => {
        chip.addEventListener('click', () => {
            chip.classList.toggle('active');
            const cb = chip.querySelector('input[type="checkbox"]');
            if (cb) cb.checked = chip.classList.contains('active');
        });
    });

    document.getElementById('filter-apply-btn').addEventListener('click', () => {
        // Read active type filters
        const typeChips = document.querySelectorAll('[data-filter-type]');
        activeFilters.types = [];
        typeChips.forEach(c => { if (c.classList.contains('active')) activeFilters.types.push(c.dataset.filterType); });

        // Read active status filters
        const statusChips = document.querySelectorAll('[data-filter-status]');
        activeFilters.statuses = [];
        statusChips.forEach(c => { if (c.classList.contains('active')) activeFilters.statuses.push(c.dataset.filterStatus); });

        // Apply to schedule table
        const rows = document.querySelectorAll('#schedule-tbody tr');
        rows.forEach(row => {
            const type = row.dataset.type;
            const status = row.dataset.status;
            const show = activeFilters.types.includes(type) && activeFilters.statuses.includes(status);
            row.style.display = show ? '' : 'none';
        });

        // Redraw chart
        drawChart();

        closeFilterModal();
        showToast('Filters applied successfully');
    });

    document.getElementById('filter-reset-btn').addEventListener('click', () => {
        filterChips.forEach(chip => {
            chip.classList.add('active');
            const cb = chip.querySelector('input[type="checkbox"]');
            if (cb) cb.checked = true;
        });
        activeFilters = { types: ['premium', 'passenger', 'freight'], statuses: ['ontime', 'delayed', 'rerouted'] };
        document.querySelectorAll('#schedule-tbody tr').forEach(row => row.style.display = '');
        drawChart();
        showToast('Filters reset');
    });

    // Close modal on overlay click
    filterModal.addEventListener('click', (e) => { if (e.target === filterModal) closeFilterModal(); });


    /* ══════════════════════════════════════════════
       9. Add Route Modal
       ══════════════════════════════════════════════ */

    const addRouteModal = document.getElementById('add-route-modal');

    function openAddRouteModal() { addRouteModal.classList.add('active'); }
    function closeAddRouteModal() {
        addRouteModal.classList.remove('active');
        document.getElementById('new-train-number').value = '';
        document.getElementById('new-train-name').value = '';
    }

    document.getElementById('btn-add-route').addEventListener('click', openAddRouteModal);
    document.getElementById('add-route-modal-close').addEventListener('click', closeAddRouteModal);
    document.getElementById('add-route-cancel-btn').addEventListener('click', closeAddRouteModal);
    addRouteModal.addEventListener('click', (e) => { if (e.target === addRouteModal) closeAddRouteModal(); });

    document.getElementById('add-route-submit-btn').addEventListener('click', () => {
        const trainNum = document.getElementById('new-train-number').value.trim();
        const trainName = document.getElementById('new-train-name').value.trim();
        const trainType = document.getElementById('new-train-type').value;
        const origin = parseInt(document.getElementById('new-origin').value);
        const destination = parseInt(document.getElementById('new-destination').value);

        if (!trainNum || !trainName) {
            showToast('Please fill in all fields', 'warning');
            return;
        }

        if (origin === destination) {
            showToast('Origin and destination cannot be the same', 'warning');
            return;
        }

        // Color based on type
        const typeColors = { passenger: '#0C1B33', premium: '#1C5D39', freight: '#D9822B' };
        const newLine = {
            id: trainNum,
            startStation: origin,
            endStation: destination,
            startTime: Math.floor(Math.random() * 60) + 20,
            endTime: Math.floor(Math.random() * 60) + 100,
            color: typeColors[trainType] || '#0C1B33',
            type: trainType
        };

        // Add to chart data
        chartConfig.lines.push(newLine);

        // Add to schedule table
        const tbody = document.getElementById('schedule-tbody');
        const now = new Date();
        const hrs = String(now.getHours()).padStart(2, '0');
        const mins = String(now.getMinutes()).padStart(2, '0');
        const timeStr = `${hrs}:${mins}`;

        const tr = document.createElement('tr');
        tr.dataset.type = trainType;
        tr.dataset.status = 'ontime';
        tr.innerHTML = `
            <td class="tabular-nums">${trainNum}</td>
            <td>${trainName}</td>
            <td><span class="type-badge ${trainType}">${trainType.charAt(0).toUpperCase() + trainType.slice(1)}</span></td>
            <td>${chartConfig.stations[origin]}</td>
            <td class="tabular-nums">${timeStr}</td>
            <td class="tabular-nums">${timeStr}</td>
            <td><span class="badge badge-success">ON TIME</span></td>
        `;
        tbody.appendChild(tr);

        // Redraw chart
        drawChart();

        closeAddRouteModal();
        showToast(`Route ${trainNum} (${trainName}) added successfully`);
        addAlert(`New route ${trainNum} ${trainName} added to network`, 'warning');
    });


    /* ══════════════════════════════════════════════
       10. Export CSV
       ══════════════════════════════════════════════ */

    function exportScheduleCSV() {
        const table = document.getElementById('schedule-table');
        const rows = table.querySelectorAll('tr');
        let csv = [];

        rows.forEach(row => {
            const cells = row.querySelectorAll('th, td');
            const rowData = [];
            cells.forEach(cell => {
                let text = cell.textContent.trim().replace(/"/g, '""');
                rowData.push(`"${text}"`);
            });
            csv.push(rowData.join(','));
        });

        const blob = new Blob([csv.join('\n')], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'train_schedule_report.csv';
        link.click();
        URL.revokeObjectURL(url);

        showToast('Schedule exported as CSV');
    }

    document.getElementById('btn-export-dashboard').addEventListener('click', exportScheduleCSV);
    document.getElementById('btn-export-schedules').addEventListener('click', exportScheduleCSV);


    /* ══════════════════════════════════════════════
       11. Download PDF & Share
       ══════════════════════════════════════════════ */

    document.getElementById('btn-download-pdf').addEventListener('click', () => {
        window.print();
    });

    document.getElementById('btn-share').addEventListener('click', () => {
        if (navigator.share) {
            navigator.share({ title: 'ITO Report', text: 'Bottleneck & Conflict Analytics Report — ITO' });
        } else {
            navigator.clipboard.writeText(window.location.href);
            showToast('Report link copied to clipboard');
        }
    });


    /* ══════════════════════════════════════════════
       12. Zoom In / Zoom Out (Network Map)
       ══════════════════════════════════════════════ */

    const networkSvg = document.getElementById('network-svg');

    document.getElementById('btn-zoom-in').addEventListener('click', () => {
        zoomLevel = Math.min(zoomLevel * 1.1, 3.0);
        networkSvg.style.transform = `scale(${zoomLevel})`;
        networkSvg.style.transformOrigin = 'center center';
    });

    document.getElementById('btn-zoom-out').addEventListener('click', () => {
        zoomLevel = Math.max(zoomLevel * 0.9, 0.5);
        networkSvg.style.transform = `scale(${zoomLevel})`;
        networkSvg.style.transformOrigin = 'center center';
    });


    /* ══════════════════════════════════════════════
       12b. Live Telemetry & DQN Neural Stream
       ══════════════════════════════════════════════ */

    const neuralStreamLog = document.getElementById('neural-stream-log');
    const neuralStream = document.querySelector('.neural-stream');
    const neuralStreamHeader = document.querySelector('.neural-stream-header');
    const terminalToggle = document.getElementById('toggle-terminal');
    const topBar = document.querySelector('.top-bar');
    const mockTelemetrySocket = { onmessage: null };
    const telemetryState = { progress: 0, arrivalOffsets: { '20643': 0, '12637': 0, 'BOXN-77': 0 } };
    const telemetryPaths = {
        '20643': { from: [260, 242], to: [500, 242] },
        'BOXN-77': { from: [305, 177], to: [500, 342] }
    };
    const neuralLogs = [
        '> [IoT] Ping received: Train 20643 at 110km/h',
        '> [DQN-AGENT] Evaluating block sector 4 density...',
        '> [MILP-CORE] Headway gap stable. Holding priority.',
        '> [SENSOR] ESP32 node 44 active. Track integrity 100%.'
    ];

    terminalToggle.addEventListener('click', () => {
        const minimized = neuralStream.classList.toggle('terminal-minimized');
        terminalToggle.textContent = minimized ? '+' : '−';
        terminalToggle.setAttribute('aria-expanded', String(!minimized));
        terminalToggle.setAttribute('aria-label', minimized ? 'Expand neural stream' : 'Minimize neural stream');
    });

    let pos1 = 0;
    let pos2 = 0;
    let pos3 = 0;
    let pos4 = 0;

    function dragMouseDown(event) {
        if (event.target.closest('#toggle-terminal')) return;
        event.preventDefault();
        const bounds = neuralStream.getBoundingClientRect();
        neuralStream.style.left = `${bounds.left}px`;
        neuralStream.style.top = `${bounds.top}px`;
        neuralStream.style.right = 'auto';
        neuralStream.style.bottom = 'auto';
        pos3 = event.clientX;
        pos4 = event.clientY;
        document.addEventListener('mousemove', elementDrag);
        document.addEventListener('mouseup', closeDragElement);
    }

    function elementDrag(event) {
        pos1 = pos3 - event.clientX;
        pos2 = pos4 - event.clientY;
        pos3 = event.clientX;
        pos4 = event.clientY;
        const maxLeft = Math.max(0, window.innerWidth - neuralStream.offsetWidth);
        const maxTop = Math.max(0, window.innerHeight - neuralStream.offsetHeight);
        const nextLeft = Math.min(Math.max(0, neuralStream.offsetLeft - pos1), maxLeft);
        const nextTop = Math.min(Math.max(0, neuralStream.offsetTop - pos2), maxTop);
        neuralStream.style.top = `${nextTop}px`;
        neuralStream.style.left = `${nextLeft}px`;
    }

    function closeDragElement() {
        document.removeEventListener('mousemove', elementDrag);
        document.removeEventListener('mouseup', closeDragElement);
    }

    neuralStreamHeader.addEventListener('mousedown', dragMouseDown);

    function pushNeuralLog(message) {
        const entry = document.createElement('div');
        entry.textContent = message;
        neuralStreamLog.appendChild(entry);
        while (neuralStreamLog.children.length > 10) neuralStreamLog.firstElementChild.remove();
        neuralStreamLog.scrollTop = neuralStreamLog.scrollHeight;
    }

    function moveTrainMarker(trainId, progress) {
        const marker = networkSvg.querySelector(`[data-train-id="${trainId}"]`);
        const path = telemetryPaths[trainId];
        if (!marker || !path) return;
        const x = path.from[0] + ((path.to[0] - path.from[0]) * progress);
        const y = path.from[1] + ((path.to[1] - path.from[1]) * progress);
        marker.setAttribute('transform', `translate(${x - path.from[0]} ${y - path.from[1]})`);
    }

    function updateTelemetry() {
        telemetryState.progress = (telemetryState.progress + 0.035) % 1;
        moveTrainMarker('20643', telemetryState.progress);
        moveTrainMarker('BOXN-77', (telemetryState.progress + 0.35) % 1);

        document.querySelectorAll('.telemetry-arrival').forEach(cell => {
            const trainId = cell.dataset.trainId;
            const delta = Math.random() > 0.5 ? 1 : -1;
            telemetryState.arrivalOffsets[trainId] += delta;
            const [hours, minutes] = cell.textContent.trim().split(':').map(Number);
            const baseMinutes = (hours * 60) + minutes - (telemetryState.arrivalOffsets[trainId] - delta);
            const nextMinutes = Math.max(0, baseMinutes + telemetryState.arrivalOffsets[trainId]);
            cell.textContent = `${String(Math.floor(nextMinutes / 60)).padStart(2, '0')}:${String(nextMinutes % 60).padStart(2, '0')}`;
            cell.classList.remove('telemetry-faster', 'telemetry-slower');
            void cell.offsetWidth;
            cell.classList.add(delta < 0 ? 'telemetry-faster' : 'telemetry-slower');
        });
    }

    mockTelemetrySocket.onmessage = updateTelemetry;

    function injectWeatherAnomaly() {
        const scenarioSelect = document.getElementById('scenario');
        scenarioSelect.value = 'weather-alert';
        topBar.classList.add('telemetry-critical');
        pushNeuralLog('> [CRITICAL] Weather anomaly detected. Recalculating global network weights...');
        addAlert('⚠️ SENSOR ALERT: Track Flooding near Madurai (MDU)', 'critical');

        const weatherLines = chartConfig.lines.map(line => ({
            ...line,
            endTime: line.endTime + Math.round((line.endTime - line.startTime) * 0.3),
            bend: true,
            bendOffset: line.type === 'freight' ? -34 : 26,
            color: line.type === 'freight' ? '#D39858' : '#85431E'
        }));
        const freight = weatherLines.find(line => line.id === 'BOXN-77');
        if (freight) {
            freight.startStation = 0;
            freight.endStation = 1;
            freight.bendOffset = -48;
            freight.dashed = true;
        }
        drawChart(weatherLines);
        networkDelayEl.textContent = '+31m';
        systemStatusEl.textContent = i18n[currentLang]?.statusConflict || 'CONFLICT';
        systemStatusEl.className = 'value critical-status';

        setTimeout(() => {
            drawChart(resolveConflicts(weatherLines));
            networkDelayEl.textContent = '+2m';
            systemStatusEl.textContent = i18n[currentLang]?.statusOptimal || 'Optimal';
            systemStatusEl.className = 'value success';
            topBar.classList.remove('telemetry-critical');
            addAlert('✅ Weather routing resolved: BOXN-77 held on Karur loop line.', 'warning');
            pushNeuralLog('> [DQN-AGENT] Freight rerouted to Karur loop. Network weights stabilized.');
        }, 2200);
    }

    pushNeuralLog('> [SYSTEM] Telemetry uplink established.');
    setInterval(() => mockTelemetrySocket.onmessage({ data: { source: 'mock-gps-uplink' } }), 2000);
    setInterval(() => pushNeuralLog(neuralLogs[Math.floor(Math.random() * neuralLogs.length)]), 1500);
    setTimeout(injectWeatherAnomaly, 20000);


    /* ══════════════════════════════════════════════
       13. What-If Simulation Engine
       ══════════════════════════════════════════════ */

    function runSimulation() {
        const scenario = document.getElementById('scenario').value;
        const autoResolve = document.getElementById('auto-resolve').checked;

        loadingOverlay.classList.remove('hidden');

        setTimeout(() => {
            let simulatedLines = chartConfig.lines.map(l => ({ ...l }));

            // Scenario-specific logic
            switch (scenario) {
                case 'track-maintenance':
                    // Block track segment between Karur and Dindigul: reroute via siding
                    simulatedLines.forEach(line => {
                        if ((line.startStation <= 2 && line.endStation >= 2) || (line.endStation <= 2 && line.startStation >= 2)) {
                            line.endTime += 25;
                            line.color = '#D9822B';
                            line.dashed = true;
                        }
                    });
                    addAlert('Track maintenance block active between Karur and Dindigul. Trains rerouted.', 'warning');
                    break;

                case 'freight-delay':
                    // Add delay offset to freight trains, show cascade
                    simulatedLines.forEach(line => {
                        if (line.type === 'freight') {
                            line.endTime += 35;
                            line.color = '#A62626';
                        } else if (line.type === 'passenger') {
                            line.endTime += 12;
                            line.color = '#D9822B';
                        }
                    });
                    addAlert('Freight delay at Node A cascading to passenger routes (+12m avg)', 'critical');
                    break;

                case 'weather-alert':
                    // Speed restriction multiplier
                    simulatedLines.forEach(line => {
                        const extension = Math.round((line.endTime - line.startTime) * 0.3);
                        line.endTime += extension;
                        line.color = '#D9822B';
                    });
                    addAlert('Weather speed restriction applied. All routes +30% transit time.', 'warning');
                    break;
            }

            // Auto-Resolve Logic
            if (autoResolve) {
                simulatedLines = resolveConflicts(simulatedLines);
                networkDelayEl.textContent = '+2m';
                systemStatusEl.textContent = i18n[currentLang]?.statusOptimal || 'Optimal';
                systemStatusEl.className = 'value success';
                conflictActive = false;
                stopConflictTimer();
                showToast('Auto-Resolve: Conflicts cleared. Network delay reduced to +2m');
                addAlert('Auto-Resolve engaged: All conflicts cleared, delay minimized to +2m', 'warning');
            } else {
                networkDelayEl.textContent = '+22m';
                systemStatusEl.textContent = i18n[currentLang]?.statusConflict || 'CONFLICT';
                systemStatusEl.className = 'value critical-status';
                conflictActive = true;
                startConflictTimer();
            }

            drawChart(simulatedLines);
            loadingOverlay.classList.add('hidden');
        }, 1500);
    }

    /**
     * Conflict resolution: straighten crossing trajectories, reroute to siding
     */
    function resolveConflicts(lines) {
        return lines.map(line => {
            // Reset delayed lines to near-original timing
            const originalDuration = 80 + Math.random() * 40;
            return {
                ...line,
                endTime: line.startTime + originalDuration,
                color: line.type === 'premium' ? '#1C5D39' : line.type === 'freight' ? '#0C1B33' : '#0C1B33',
                dashed: false,
                highlight: false
            };
        });
    }

    runSimulationBtn.addEventListener('click', runSimulation);


    /* ══════════════════════════════════════════════
       14. Domino Delay Alert Interaction
       ══════════════════════════════════════════════ */

    function addAlert(message, type = 'warning') {
        const feed = document.querySelector('.alert-feed');
        const alertsContainer = feed.querySelector('.alerts-container') || feed;
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert ${type}`;

        const now = new Date();
        const timeStr = now.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit' });

        alertDiv.innerHTML = `
            <span class="alert-time">${timeStr}</span>
            <span class="alert-msg">${message}</span>
        `;
        alertDiv.dataset.station = 'Dindigul Junction (DG)';

        // Insert after the h2 heading
        const firstAlert = alertsContainer.querySelector('.alert');
        if (firstAlert) {
            alertsContainer.insertBefore(alertDiv, firstAlert);
        } else {
            alertsContainer.appendChild(alertDiv);
        }

        // Bind click handler
        alertDiv.addEventListener('click', () => handleAlertClick(alertDiv));
        return alertDiv;
    }

    function handleAlertClick(alertEl) {
        const stationName = alertEl.dataset.station || 'Dindigul Junction (DG)';

        // Reset user activity — conflict acknowledged
        resetConflictTimer();

        // 1. Highlight station on network map
        highlightStationOnMap(stationName);

        // 2. Flash conflicting lines on string chart
        flashConflictLines();

        // 3. Switch to network map tab
        const networkTab = document.querySelector('[data-tab="network-map"]');
        if (networkTab) networkTab.click();

        alertEl.classList.add('highlight-pulse');
        setTimeout(() => alertEl.classList.remove('highlight-pulse'), 2000);
    }

    function highlightStationOnMap(stationName) {
        const circles = document.querySelectorAll(`circle[data-station="${stationName}"]`);
        circles.forEach(circle => {
            circle.classList.add('station-highlight');
            setTimeout(() => circle.classList.remove('station-highlight'), 3500);
        });
    }

    function flashConflictLines() {
        const originalLines = chartConfig.lines.map(l => ({ ...l }));
        const flashLines = originalLines.map(l => ({
            ...l,
            color: l.id === '20643' ? '#85431E' : l.color,
            highlight: l.id === '20643'
        }));

        let flashCount = 0;
        const flashInterval = setInterval(() => {
            if (flashCount % 2 === 0) {
                drawChart(flashLines);
            } else {
                drawChart(originalLines);
            }
            flashCount++;
            if (flashCount >= 6) {
                clearInterval(flashInterval);
                drawChart();
            }
        }, 300);
    }

    // Bind existing alert elements
    document.querySelectorAll('.alert').forEach(alertEl => {
        alertEl.addEventListener('click', () => handleAlertClick(alertEl));
    });


    /* ══════════════════════════════════════════════
       15. Autonomous Escalation & Deadman Protocol
       ══════════════════════════════════════════════ */

    const conflictCountdownEl = document.getElementById('conflict-countdown');
    const countdownFill = document.getElementById('countdown-fill');
    const countdownTimeEl = document.getElementById('countdown-time');
    const escalationModal = document.getElementById('escalation-modal');

    function startConflictTimer() {
        if (!conflictActive || escalationTriggered || isModalOpen) return;

        conflictCountdownSec = 15;
        conflictCountdownEl.classList.add('active');
        updateCountdownDisplay();

        conflictTimerId = setInterval(() => {
            conflictCountdownSec--;
            updateCountdownDisplay();

            if (conflictCountdownSec <= 0) {
                clearInterval(conflictTimerId);
                conflictTimerId = null;
                triggerEscalation();
            }
        }, 1000);
    }

    function stopConflictTimer() {
        if (conflictTimerId) {
            clearInterval(conflictTimerId);
            conflictTimerId = null;
        }
        conflictCountdownEl.classList.remove('active');
    }

    function resetConflictTimer() {
        stopConflictTimer();
        if (conflictActive && !escalationTriggered) {
            startConflictTimer();
        }
    }

    function updateCountdownDisplay() {
        const pct = (conflictCountdownSec / 15) * 100;
        countdownFill.style.width = pct + '%';
        countdownTimeEl.textContent = conflictCountdownSec + 's';
    }

    function triggerEscalation() {
        if (isModalOpen) return;
        escalationTriggered = true;
        isModalOpen = true;
        escalationModal.classList.add('active');
        escalationToast = showToast('🚨 ESCALATION: Unattended conflict auto-escalated', 'critical');
        if (!document.querySelector('.alert[data-escalation="true"]')) {
            const escalationAlert = addAlert('🚨 AUTONOMOUS ESCALATION: Conflict at Dindigul Junction (DG) unattended for 15s. SOS broadcast transmitted.', 'critical');
            escalationAlert.dataset.escalation = 'true';
        }
    }

    function resolveEscalation(actionType) {
        if (!isModalOpen) return;
        escalationModal.classList.remove('active');
        dismissEscalationToast();
        isModalOpen = false;
        escalationTriggered = false;
        conflictActive = false;
        stopConflictTimer();

        // Update system status
        systemStatusEl.textContent = i18n[currentLang]?.statusOptimal || 'Optimal';
        systemStatusEl.className = 'value success';
        networkDelayEl.textContent = '+2m';

        // Log to audit trail
        const now = new Date();
        const timestamp = now.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit' });

        const actionText = actionType === 'siding'
            ? 'Auto-Route Freight to Siding Loop B (P1 Safety Clear)'
            : 'Enforce Section Red Hold Signal';

        const entry = {
            timestamp,
            event: 'Unattended Critical Conflict — Dindigul Junction (DG) (Track Sector 4)',
            action: actionText,
            operator: 'Admin (Autonomous Override)',
            result: 'RESOLVED — System Optimal'
        };
        auditLog.push(entry);
        renderAuditLog();

        showToast(`✅ Conflict resolved: ${actionText}`);
        addAlert(`✅ Escalation resolved: ${actionText}. System status OPTIMAL.`, 'warning');

        // Redraw chart with resolved lines
        drawChart(resolveConflicts(chartConfig.lines.map(l => ({ ...l }))));
    }

    // Escalation action buttons
    document.getElementById('esc-action-siding').addEventListener('click', () => resolveEscalation('siding'));
    document.getElementById('esc-action-red-signal').addEventListener('click', () => resolveEscalation('red-signal'));

    function renderAuditLog() {
        const tbody = document.getElementById('audit-tbody');
        tbody.innerHTML = '';
        auditLog.forEach(entry => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="tabular-nums">${entry.timestamp}</td>
                <td>${entry.event}</td>
                <td>${entry.action}</td>
                <td>${entry.operator}</td>
                <td><span class="badge badge-success">${entry.result}</span></td>
            `;
            tbody.prepend(tr);
        });
    }

    // Reset conflict timer on ANY user interaction (Deadman protocol)
    ['click', 'keydown', 'mousemove', 'touchstart'].forEach(evt => {
        document.addEventListener(evt, () => {
            if (conflictActive && !escalationTriggered && conflictTimerId) {
                resetConflictTimer();
            }
        }, { passive: true });
    });


    /* ══════════════════════════════════════════════
       16. Window Resize Handler
       ══════════════════════════════════════════════ */

    window.addEventListener('resize', () => {
        if (!stages['dashboard'].classList.contains('hidden')) {
            resizeCanvas();
        }
    });


    /* ══════════════════════════════════════════════
       17. Initialization
       ══════════════════════════════════════════════ */

    setTimeout(resizeCanvas, 80);

    // Start conflict timer (there's an active critical conflict at load)
    setTimeout(() => {
        startConflictTimer();
    }, 2000);

});
