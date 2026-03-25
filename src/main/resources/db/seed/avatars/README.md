# Avatar seed images

Place profile images here — they are loaded automatically on startup
(default and test profiles) by `AvatarSeeder`.

## Naming convention

Filename = employee UUID + image extension:

```
00000000-0000-0000-0000-000000000001.png  ← Marcus Karlsson
00000000-0000-0000-0000-000000000010.jpg   ← Philip Olsson
00000000-0000-0000-0000-000000000011.jpg   ← Philip Schill
00000000-0000-0000-0000-000000000013.png   ← Petra Lichtenecker
```

Supported formats: `.jpg` / `.jpeg`, `.png`, `.webp`

Files with names that are not valid UUIDs are silently skipped.
If an employee ID does not exist in the database the file is silently skipped.
