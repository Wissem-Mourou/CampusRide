<?php
require_once "db.php";

$nom = isset($_POST['nom']) ? trim($_POST['nom']) : "";
$prenom = isset($_POST['prenom']) ? trim($_POST['prenom']) : "";
$email = isset($_POST['email_universitaire']) ? trim($_POST['email_universitaire']) : "";
$password = isset($_POST['mot_de_passe']) ? $_POST['mot_de_passe'] : "";
$ville = isset($_POST['ville']) ? trim($_POST['ville']) : "";
$telephone = isset($_POST['telephone']) ? trim($_POST['telephone']) : "";

if (empty($nom) || empty($prenom) || empty($email) || empty($password) || empty($ville) || empty($telephone)) {
    sendResponse(false, null, "Tous les champs obligatoires doivent être renseignés.");
}

// Vérification de l'adresse email universitaire (.tn, .rnu.tn, .edu)
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendResponse(false, null, "Format d'adresse email invalide.");
}

$allowedDomains = ['.tn', '.rnu.tn', '.edu', 'isetn.rnu.tn', 'univ.tn'];
$isAllowed = false;
foreach ($allowedDomains as $d) {
    if (str_ends_with(strtolower($email), $d)) {
        $isAllowed = true;
        break;
    }
}

if (!$isAllowed) {
    sendResponse(false, null, "L'adresse email doit appartenir à un domaine universitaire tunisien (.tn ou .rnu.tn).");
}

try {
    // Vérifier si l'email existe déjà
    $stmt = $pdo->prepare("SELECT id FROM utilisateur WHERE email_universitaire = ?");
    $stmt->execute([$email]);
    if ($stmt->fetch()) {
        sendResponse(false, null, "Cette adresse email universitaire est déjà utilisée.");
    }

    // Hachage sécurisé du mot de passe
    $hashedPassword = password_hash($password, PASSWORD_BCRYPT);

    $insert = $pdo->prepare("INSERT INTO utilisateur (nom, prenom, email_universitaire, mot_de_passe, ville, telephone, note_moyenne) VALUES (?, ?, ?, ?, ?, ?, 5.0)");
    $insert->execute([$nom, $prenom, $email, $hashedPassword, $ville, $telephone]);
    $userId = (int)$pdo->lastInsertId();

    sendResponse(true, [
        "id" => $userId,
        "nom" => $nom,
        "prenom" => $prenom,
        "email_universitaire" => $email,
        "ville" => $ville,
        "telephone" => $telephone,
        "note_moyenne" => 5.0
    ], "Inscription réussie !");

} catch (PDOException $e) {
    sendResponse(false, null, "Erreur serveur : " . $e->getMessage());
}
?>
