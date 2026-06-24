import secrets
import string
import uuid as uuid_module


def uuid() -> str:
    return str(uuid_module.uuid4())


def random_string(length: int) -> str:
    alphabet = string.ascii_letters + string.digits
    return "".join(secrets.choice(alphabet) for _ in range(length))
