package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.DbMpaStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class MpaService {

    private final DbMpaStorage dbMpaStorage;

    public Collection<Mpa> getAllMpa() {
        return dbMpaStorage.getAllMpa();
    }

    public Mpa getMpa(int mpaId) {
        return dbMpaStorage.getMpa(mpaId);
    }

}

