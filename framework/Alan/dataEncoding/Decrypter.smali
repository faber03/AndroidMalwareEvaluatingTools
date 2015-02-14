.class public Lcom123456789/Decrypter;
.super Ljava/lang/Object;
.source "Decrypter.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 6
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static applyCaesar(Ljava/lang/String;)Ljava/lang/String;
    .locals 8
    .param p0, "text"    # Ljava/lang/String;

    .prologue
    const/16 v7, 0x20

    .line 10
    const/4 v3, -0x2

    .line 11
    .local v3, "shift":I
    invoke-virtual {p0}, Ljava/lang/String;->toCharArray()[C

    move-result-object v1

    .line 12
    .local v1, "chars":[C
    const/4 v4, 0x0

    .line 13
    .local v4, "skip":Z
    const/4 v2, 0x0

    .local v2, "i":I
    :goto_0
    invoke-virtual {p0}, Ljava/lang/String;->length()I

    move-result v6

    if-lt v2, v6, :cond_0

    .line 53
    new-instance v6, Ljava/lang/String;

    invoke-direct {v6, v1}, Ljava/lang/String;-><init>([C)V

    return-object v6

    .line 15
    :cond_0
    aget-char v0, v1, v2

    .line 16
    .local v0, "c":C
    const/16 v6, 0x5c

    if-ne v0, v6, :cond_2

    .line 18
    const/4 v4, 0x1

    .line 13
    :cond_1
    :goto_1
    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    .line 21
    :cond_2
    const/16 v6, 0x22

    if-eq v0, v6, :cond_1

    .line 23
    if-eq v0, v7, :cond_1

    .line 25
    const/16 v6, 0xa

    if-eq v0, v6, :cond_1

    .line 27
    const/16 v6, 0x9

    if-eq v0, v6, :cond_1

    .line 29
    const/16 v6, 0x27

    if-eq v0, v6, :cond_1

    .line 31
    const/16 v6, 0x5a

    if-eq v0, v6, :cond_1

    .line 35
    if-lt v0, v7, :cond_1

    const/16 v6, 0x7f

    if-gt v0, v6, :cond_1

    .line 38
    if-eq v0, v7, :cond_3

    if-nez v4, :cond_1

    .line 43
    :cond_3
    const/4 v4, 0x0

    .line 46
    add-int/lit8 v5, v0, -0x20

    .line 47
    .local v5, "x":I
    add-int v6, v5, v3

    rem-int/lit8 v5, v6, 0x60

    .line 48
    if-gez v5, :cond_4

    .line 49
    add-int/lit8 v5, v5, 0x60

    .line 50
    :cond_4
    add-int/lit8 v6, v5, 0x20

    int-to-char v6, v6

    aput-char v6, v1, v2

    goto :goto_1
.end method
