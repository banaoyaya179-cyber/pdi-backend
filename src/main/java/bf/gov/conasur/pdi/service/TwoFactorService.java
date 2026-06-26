package bf.gov.conasur.pdi.service;

import bf.gov.conasur.pdi.dto.response.Setup2FAResponse;
import bf.gov.conasur.pdi.entity.Utilisateur;
import bf.gov.conasur.pdi.exception.ResourceNotFoundException;
import bf.gov.conasur.pdi.repository.UtilisateurRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TwoFactorService {

    private final UtilisateurRepository utilisateurRepository;
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    // Générer le secret et le QR code pour un utilisateur
    public Setup2FAResponse setup(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));

        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        String secret = key.getKey();

        // Sauvegarder le secret (pas encore activé)
        user.setSecret2FA(secret);
        utilisateurRepository.save(user);

        String otpAuthUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                "PDI-Burkina", email, key);

        // URL QR via Google Charts API
        String qrCodeUrl = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl="
                + java.net.URLEncoder.encode(otpAuthUrl, java.nio.charset.StandardCharsets.UTF_8);

        return new Setup2FAResponse(secret, otpAuthUrl, qrCodeUrl);
    }

    // Activer le 2FA après vérification du premier code
    public boolean activer(String email, int code) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (user.getSecret2FA() == null) return false;

        boolean valide = googleAuthenticator.authorize(user.getSecret2FA(), code);
        if (valide) {
            user.setDoubleAuthActive(true);
            utilisateurRepository.save(user);
        }
        return valide;
    }

    // Vérifier le code lors de la connexion
    public boolean verifier(String email, int code) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!user.isDoubleAuthActive() || user.getSecret2FA() == null) return true;

        return googleAuthenticator.authorize(user.getSecret2FA(), code);
    }

    // Désactiver le 2FA
    public void desactiver(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setDoubleAuthActive(false);
        user.setSecret2FA(null);
        utilisateurRepository.save(user);
    }
}
