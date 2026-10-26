-- Sample placeholder pieces (original text, safe to replace with your own writing)

INSERT INTO pieces (title, slug, language, type, body, author_name, reading_time_minutes, published_on, status, featured) VALUES
                                                                                                                              ('The Last Train Home', 'the-last-train-home', 'EN', 'STORY',
                                                                                                                               'The station clock had stopped years ago, but Meera still arrived at six every evening. She carried two cups of tea and a habit she could not name. Trains came and went, and strangers hurried past with their own small urgencies. When the last train pulled out, she poured the second cup onto the tracks, the way her father used to, and walked home in the blue dusk. Some goodbyes, she thought, take longer than others.',
                                                                                                                               'Sample Author', 2, '2026-10-01', 'PUBLISHED', TRUE),

                                                                                                                              ('After the Rain', 'after-the-rain', 'EN', 'JOURNAL',
                                                                                                                               'It rained until noon today. I sat by the window with my tea and let the hours go slowly. There was nothing to finish and nowhere to be. When the sky cleared in the evening, it felt as if the day itself had taken a long breath. I want more days like this.',
                                                                                                                               'Sample Author', 1, '2026-10-05', 'PUBLISHED', FALSE),

                                                                                                                              ('बारिश के बाद', 'barish-ke-baad', 'HI', 'JOURNAL',
                                                                                                                               'आज सुबह देर तक बारिश होती रही। मैं खिड़की के पास बैठा चाय पीता रहा और सोचता रहा कि कुछ दिन बिना किसी जल्दी के भी गुज़र सकते हैं। शाम को आसमान साफ़ हुआ तो लगा, जैसे दिन ने भी थोड़ी साँस ली हो।',
                                                                                                                               'नमूना लेखक', 1, '2026-10-06', 'PUBLISHED', FALSE),

                                                                                                                              ('शाम ढली तो', 'shaam-dhali-to', 'HI', 'GHAZAL',
                                                                                                                               E'शाम ढली तो यादों का दीया जल गया,\nएक पुराना मौसम फिर से मचल गया।\n\nजो कह न सका मैं, लबों तक आकर भी,\nवही ख़ामोशी बनकर शेर में ढल गया।',
                                                                                                                               'नमूना लेखक', 1, '2026-10-08', 'PUBLISHED', FALSE),

                                                                                                                              ('On Small Hopes', 'on-small-hopes', 'EN', 'QUOTE',
                                                                                                                               'Hope does not always arrive as a sunrise. Sometimes it is only a window left open.',
                                                                                                                               'Sample Author', 1, '2026-10-10', 'PUBLISHED', FALSE),

                                                                                                                              ('सब्र का फल', 'sabr-ka-phal', 'HI', 'QUOTE',
                                                                                                                               'हर देर से मिली राहत, किसी सब्र का इनाम होती है।',
                                                                                                                               'नमूना लेखक', 1, '2026-10-11', 'PUBLISHED', FALSE);

-- Mood links
INSERT INTO piece_moods (piece_id, mood_id)
SELECT p.id, m.id FROM pieces p, moods m WHERE
    (p.slug = 'the-last-train-home' AND m.slug IN ('nostalgic', 'melancholic', 'lonely')) OR
    (p.slug = 'after-the-rain'      AND m.slug IN ('peaceful', 'reflective')) OR
    (p.slug = 'barish-ke-baad'      AND m.slug IN ('peaceful', 'comforted')) OR
    (p.slug = 'shaam-dhali-to'      AND m.slug IN ('nostalgic', 'romantic', 'lonely')) OR
    (p.slug = 'on-small-hopes'      AND m.slug IN ('hopeful', 'comforted')) OR
    (p.slug = 'sabr-ka-phal'        AND m.slug IN ('hopeful', 'reflective'));

-- Collections
INSERT INTO collections (name, slug, description, sort_order, published) VALUES
                                                                             ('For a Quiet Evening', 'for-a-quiet-evening', 'Gentle pieces to read as the day winds down.', 1, TRUE),
                                                                             ('A Little Hope', 'a-little-hope', 'Short writing for days when you need a small light.', 2, TRUE);

INSERT INTO collection_pieces (collection_id, piece_id, position)
SELECT c.id, p.id, 0 FROM collections c, pieces p WHERE
    (c.slug = 'for-a-quiet-evening' AND p.slug IN ('after-the-rain', 'barish-ke-baad', 'shaam-dhali-to')) OR
    (c.slug = 'a-little-hope'       AND p.slug IN ('on-small-hopes', 'sabr-ka-phal', 'the-last-train-home'));