package by.vorivoda.matvey.trash;

import by.vorivoda.matvey.model.dao.entity.repository.TokenRepository;
import by.vorivoda.matvey.security.SecurityToken;
import by.vorivoda.matvey.model.service.DAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Deprecated
public class TokenService implements DAOService<SecurityToken> {

    private final TokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public boolean create(SecurityToken token) {
        if (tokenRepository.findByValue(token.getValue()) != null) return false;

        token.setValue(passwordEncoder.encode(token.getValue()));
        tokenRepository.save(token);
        return true;
    }

    @Override
    public SecurityToken get(Long id) {
        return tokenRepository.getById(id);
    }

    @Override
    public List<SecurityToken> getAll() {
        return tokenRepository.findAll();
    }

    @Override
    public boolean update(Long id, SecurityToken token) {
        if (delete(id)) {
            create(token);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        if (tokenRepository.existsById(id)) {
            tokenRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
