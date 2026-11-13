#!/usr/bin/env python3
"""Prepare Horizon Android assets using the exact Minecraft manifest pinned by Horizon.

Mojang can republish the version manifest with a different asset-index hash.
The upstream prepare script validates its input against research-lock.json, so
force its manifest lookup to the repository's pinned package URL rather than
using whichever republished manifest Mojang currently lists.
"""
import json
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
HORIZON = ROOT / "third_party" / "horizon"
sys.path.insert(0, str(HORIZON / "tools"))

import android_prepare  # noqa: E402

lock = json.loads((HORIZON / "research-lock.json").read_text(encoding="utf-8"))
minecraft = lock["minecraft"]
version = minecraft["version"]
pinned_url = (
    "https://piston-meta.mojang.com/v1/packages/"
    f"{minecraft['manifest_sha1']}/{version}.json"
)
original_fetch_json = android_prepare.fetch_json


def fetch_json(url):
    result = original_fetch_json(url)
    if url == android_prepare.MANIFEST:
        for entry in result.get("versions", []):
            if entry.get("id") == version:
                entry["url"] = pinned_url
                entry["sha1"] = minecraft["manifest_sha1"]
                break
        else:
            raise RuntimeError(f"Mojang version catalog no longer lists pinned Minecraft {version}")
    return result


android_prepare.fetch_json = fetch_json
android_prepare.main()
